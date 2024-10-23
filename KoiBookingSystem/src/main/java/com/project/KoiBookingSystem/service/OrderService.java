package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.*;
import com.project.KoiBookingSystem.enums.*;
import com.project.KoiBookingSystem.exception.*;
import com.project.KoiBookingSystem.model.request.DeliveredDateRequest;
import com.project.KoiBookingSystem.model.request.OrderRequest;
import com.project.KoiBookingSystem.model.response.EmailDetail;
import com.project.KoiBookingSystem.model.response.OrderDetailResponse;
import com.project.KoiBookingSystem.model.response.OrderResponse;
import com.project.KoiBookingSystem.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    OrderPaymentRepository orderPaymentRepository;

    @Autowired
    PaymentService paymentService;

    @Autowired
    KoiRepository koiRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    OrdersRepository ordersRepository;

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    TourRepository tourRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    VNPayService vnPayService;

    @Transactional
    public OrderResponse createNewOrder(OrderRequest orderRequest, String tourId, String customerId) {
        Tour tour = tourRepository.findTourByTourId(tourId);
        validateTour(tour);

        Account consulting = authenticationService.getCurrentAccount();
        validateConsultingStaff(consulting, tour);
        Account customer = getCustomer(customerId);
        validateCustomerBooking(tour, customer);

        Orders newOrder = createOrder(orderRequest, tour, customer);
        List<OrderDetail> orderDetails = createOrderDetails(orderRequest, tour, newOrder);
        newOrder.setOrderDetails(orderDetails);

        double totalPrice = calculateTotalPrice(orderDetails);
        newOrder.setTotalPrice(totalPrice);
        newOrder.setPaidPrice(totalPrice * 0.7);
        newOrder.setRemainingPrice(totalPrice * 0.3);

        Orders savedOrder = ordersRepository.save(newOrder);
        return convertToOrdersResponse(savedOrder);
    }


    public List<OrderResponse> getAllOrdersByCustomer(String customerId) { //
        List<Orders> orders = ordersRepository.findByCustomer_UserId(customerId);
        if (orders.isEmpty()) {
            throw new EmptyListException("There is no order in the list!");
        }
        return orders.stream().map(this::convertToOrdersResponse).collect(Collectors.toList());
    }


    public List<OrderResponse> getAllOrdersByTour(String tourId) {
        List<Orders> orders = ordersRepository.findByTour_TourId(tourId);
        if (orders.isEmpty()) {
            throw new EmptyListException("There is no order in the list!");
        }
        return orders.stream().map(this::convertToOrdersResponse).collect(Collectors.toList());
    }


    @Transactional
    public OrderResponse updateOrderStatusByFarmHost(String orderId, OrderStatus status) {
        Orders order = ordersRepository.findByOrderId(orderId);
        validateOrder(order);
        Account farmHost = authenticationService.getCurrentAccount();
        if (farmHost == null || !farmHost.getRole().equals(Role.FARM_HOST)) {
            throw new ActionException("You are not allowed to update order status!");
        }
        if (!status.equals(OrderStatus.PREPARING) && !status.equals(OrderStatus.SHIPPING)) {
            throw new ActionException("You are not allowed to modify these order status!");
        }
        order.setStatus(status);
        Orders savedOrder = ordersRepository.save(order);
        return convertToOrdersResponse(savedOrder);
    }


    @Transactional
    public OrderResponse updateOrderStatusByConsulting(String orderId, OrderStatus status) {
        Orders order = ordersRepository.findByOrderId(orderId);
        validateOrder(order);
        Account consulting = authenticationService.getCurrentAccount();
        if (consulting == null || !consulting.getRole().equals(Role.CONSULTING)) {
            throw new ActionException("You are not allowed to update order status!");
        }
        if (!order.getStatus().equals(OrderStatus.SHIPPING)) {
            throw new ActionException("This order can not be modified yet!");
        }
        if (!status.equals(OrderStatus.RECEIVED)) {
            throw new ActionException("You are not allowed to modify these order status!");
        }
        order.setStatus(status);
        Orders savedOrder = ordersRepository.save(order);
        return convertToOrdersResponse(savedOrder);
    }


    @Transactional
    public OrderResponse updateOrderStatusByDelivering(String orderId, OrderStatus status) {
        Orders order = ordersRepository.findByOrderId(orderId);
        validateOrder(order);
        Account delivering = authenticationService.getCurrentAccount();
        if (delivering == null || !delivering.getRole().equals(Role.DELIVERING)) {
            throw new ActionException("You are not allowed to update order status!");
        }
        if (!order.getStatus().equals(OrderStatus.RECEIVED) && !order.getStatus().equals(OrderStatus.DELIVERING)) {
            throw new ActionException("This order can not be modified yet!");
        }
        if (!status.equals(OrderStatus.DELIVERING)) {
            throw new ActionException("You are not allowed to modify these order status!");
        }
        order.setStatus(status);
        Orders savedOrder = ordersRepository.save(order);
        return convertToOrdersResponse(savedOrder);
    }


    @Transactional
    public void createFirstOrderTransaction(String orderId) {
        Orders order = ordersRepository.findByOrderId(orderId);
        validateOrder(order);
        if (!order.getPayments().isEmpty()) {
            OrdersPayment firstOrdersPayment = order.getPayments().get(0);
            Payment firstPayment = firstOrdersPayment.getPayment();
            if (firstPayment.getStatus().equals(PaymentStatus.COMPLETED)) {
                throw new ActionException("First payment of this order was processed! Can not be paid again!");
            }
        }
        OrdersPayment ordersPayment = createOrderPayment(order, OrderPaymentStatus.FIRST_PAYMENT);
        Payment payment = ordersPayment.getPayment();
        try {
            Set<Transactions> transactionsSet = createOrderTransactions(payment, orderId, OrderPaymentStatus.FIRST_PAYMENT);

            payment.setTransactions(transactionsSet);
            payment.setStatus(PaymentStatus.COMPLETED);

            order.getPayments().add(ordersPayment);

            order.setStatus(OrderStatus.PREPARING);
            ordersRepository.save(order);

            paymentRepository.save(payment);
            sendOrderConfirmation(order.getCustomer(), order);
        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new PaymentException("First Payment transaction failed: " + e.getMessage());
        }
    }


    @Transactional
    public void createFinalOrderTransaction(String orderId) {
        Orders order = ordersRepository.findByOrderId(orderId);
        validateOrder(order);

        if (!order.getStatus().equals(OrderStatus.DELIVERING)) {
            throw new InvalidRequestException("This payment only be made when the order status is on delivering!");
        }
        if (order.getPayments().isEmpty()) {
            throw new InvalidRequestException("This order can not be make final payment because the first payment is not existed!");
        }
        OrdersPayment firstOrdersPayment = order.getPayments().get(0);
        Payment firstPayment = firstOrdersPayment.getPayment();
        if (!firstPayment.getStatus().equals(PaymentStatus.COMPLETED)) {
            throw new InvalidRequestException("This order is not allowed to make the final payment!");
        }
        OrdersPayment finalOrdersPayment = createOrderPayment(order, OrderPaymentStatus.FINAL_PAYMENT);
        Payment finalPayment = finalOrdersPayment.getPayment();

        try {
            Set<Transactions> transactionsSet = createOrderTransactions(finalPayment, orderId, OrderPaymentStatus.FINAL_PAYMENT);

            finalPayment.setTransactions(transactionsSet);
            finalPayment.setStatus(PaymentStatus.COMPLETED);

            order.getPayments().add(finalOrdersPayment);

            order.setStatus(OrderStatus.DELIVERED);
            ordersRepository.save(order);

            sendOrderDeliveredSuccessfully(order.getCustomer(), order);
        } catch (Exception e) {
            finalPayment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(finalPayment);
            throw new PaymentException("Final payment transaction failed: " + e.getMessage());
        }
    }


    @Transactional
    public OrderResponse updateExpectedDeliveredDate(String orderId, DeliveredDateRequest deliveredDateRequest) {
        Orders order = ordersRepository.findByOrderId(orderId);
        validateOrder(order);
        if (order.getPayments().isEmpty()) {
            throw new InvalidRequestException("This order has not been paid yet!");
        }
        OrdersPayment firstOrdersPayment = order.getPayments().get(0);
        Payment firstPayment = firstOrdersPayment.getPayment();
        if (!firstPayment.getStatus().equals(PaymentStatus.COMPLETED)) {
            throw new InvalidRequestException("This order is not allowed to make the final payment!");
        }
        Account consulting = authenticationService.getCurrentAccount();
        validateConsultingStaff(consulting, order.getTour());

        order.setDeliveredDate(deliveredDateRequest.getDeliveredDate());
        Orders savedOrder = ordersRepository.save(order);

        return convertToOrdersResponse(savedOrder);
    }


    @Transactional
    public void handleCashFirstPayment(String orderId, PaymentCurrency currency) {
        Orders order = ordersRepository.findByOrderId(orderId);
        validateOrder(order);

        Account consulting = authenticationService.getCurrentAccount();
        validateConsultingStaff(consulting, order.getTour());

        if (!order.getPayments().isEmpty()) {
            OrdersPayment firstOrdersPayment = order.getPayments().get(0);
            Payment firstPayment = firstOrdersPayment.getPayment();
            if (firstPayment.getStatus().equals(PaymentStatus.COMPLETED)) {
                throw new InvalidRequestException("This order has been paid, can not be processed first payment!");
            }
        }
        Payment payment = new Payment();
        payment.setPaymentId(paymentService.generatePaymentId());
        payment.setBooking(null);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setDescription("Thanh toan bang tien mat");
        payment.setPaymentType(PaymentType.ORDER);
        payment.setMethod(PaymentMethod.CASH);
        payment.setCurrency(currency);
        payment.setPrice(order.getPaidPrice());
        payment.setStatus(PaymentStatus.COMPLETED);

        OrdersPayment ordersPayment = new OrdersPayment();
        ordersPayment.setOrders(order);
        ordersPayment.setPayment(payment);
        ordersPayment.setStatus(OrderPaymentStatus.FIRST_PAYMENT);
        try {
            order.getPayments().add(ordersPayment);
            paymentRepository.save(payment);
            orderPaymentRepository.save(ordersPayment);
        } catch (Exception e) {
            throw new PaymentException("Failed to process cash payment: " + e.getMessage());
        }
        order.setStatus(OrderStatus.PREPARING);
        ordersRepository.save(order);
    }

    @Transactional
    public void handleCashFinalPayment(String orderId, PaymentCurrency currency) {
        Orders order = ordersRepository.findByOrderId(orderId);
        validateOrder(order);

        Account consulting = authenticationService.getCurrentAccount();
        validateConsultingStaff(consulting, order.getTour());

        if (order.getPayments().isEmpty()) {
            throw new InvalidRequestException("This order can not be process final payment as the first payment not found!");
        }
        OrdersPayment firstOrdersPayment = order.getPayments().get(0);
        Payment firstPayment = firstOrdersPayment.getPayment();
        if (!firstPayment.getStatus().equals(PaymentStatus.COMPLETED)) {
            throw new InvalidRequestException("The first payment has not been paid, can not be processed final payment!");
        }
        Payment payment = new Payment();
        payment.setPaymentId(paymentService.generatePaymentId());
        payment.setBooking(null);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setDescription("Thanh toan bang tien mat");
        payment.setPaymentType(PaymentType.ORDER);
        payment.setMethod(PaymentMethod.CASH);
        payment.setCurrency(currency);
        payment.setPrice(order.getRemainingPrice());
        payment.setStatus(PaymentStatus.COMPLETED);

        OrdersPayment ordersPayment = new OrdersPayment();
        ordersPayment.setOrders(order);
        ordersPayment.setPayment(payment);
        ordersPayment.setStatus(OrderPaymentStatus.FINAL_PAYMENT);
        try {
            order.getPayments().add(ordersPayment);
            paymentRepository.save(payment);
            orderPaymentRepository.save(ordersPayment);
        } catch (Exception e) {
            throw new PaymentException("Failed to process cash payment: " + e.getMessage());
        }
        order.setStatus(OrderStatus.DELIVERED);
        sendOrderDeliveredSuccessfully(order.getCustomer(), order);
        ordersRepository.save(order);
    }

    private OrdersPayment createOrderPayment(Orders order, OrderPaymentStatus status) {
        Payment payment = new Payment();
        payment.setPaymentId(paymentService.generatePaymentId());
        payment.setBooking(null);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setDescription("Thanh toan bang VNPAY: " + order.getOrderId());
        payment.setPaymentType(PaymentType.ORDER);
        payment.setMethod(PaymentMethod.BANKING);
        payment.setCurrency(PaymentCurrency.VND);
        payment.setStatus(PaymentStatus.PENDING);
        if (status.equals(OrderPaymentStatus.FIRST_PAYMENT)) {
            payment.setPrice(order.getPaidPrice());
        } else {
            payment.setPrice(order.getRemainingPrice());
        }
        OrdersPayment ordersPayment = new OrdersPayment();
        ordersPayment.setOrders(order);
        ordersPayment.setPayment(payment);
        ordersPayment.setStatus(status);

        payment.setOrders(ordersPayment);
        paymentRepository.save(payment);

        return ordersPayment;
    }

    public String createOrderPaymentUrl(String orderId, boolean isFinalPayment) {
        Orders order = ordersRepository.findByOrderId(orderId);
        validateOrder(order);
        double amount = 0;
        if (isFinalPayment) {
            amount = order.getRemainingPrice();
        } else {
            amount = order.getPaidPrice();
        }
        try {
            return vnPayService.createPaymentUrl(orderId, amount, "Order");
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new PaymentException("Payment error: " + e.getMessage());
        }

    }

    private Transactions createTransaction(Account fromAccount, Account toAccount, Payment payment, String description, double amount) {
        Transactions transaction = new Transactions();
        try {
            transaction.setFromAccount(fromAccount);
            transaction.setToAccount(toAccount);
            transaction.setPayment(payment);
            transaction.setDescription(description);
            transaction.setAmount(amount);
            transaction.setStatus(TransactionsEnum.SUCCESS);

            return transaction;
        } catch (Exception e) {
            transaction.setStatus(TransactionsEnum.FAILED);
            throw new PaymentException("Transaction initiation failed!");
        }
    }

    private Set<Transactions> createOrderTransactions(Payment payment, String orderId, OrderPaymentStatus status) {
        Set<Transactions> transactionsSet = new HashSet<>();
        Orders order = ordersRepository.findByOrderId(orderId);
        validateOrder(order);

        double paymentAmount;
        if (status.equals(OrderPaymentStatus.FINAL_PAYMENT)) {
            paymentAmount = order.getRemainingPrice();
        } else {
            paymentAmount = order.getPaidPrice();
        }

        Account admin = accountRepository.findAccountByRole(Role.ADMIN);
        if (admin == null || !admin.getRole().equals(Role.ADMIN)) {
            throw new NotFoundException("There is no admin account in the system!");
        }

        double adminAmount = paymentAmount * 0.1;
        transactionsSet.add(createTransaction(null, order.getCustomer(), payment, "Transaction 1: VNPay to Customer", 0));


        transactionsSet.add(createTransaction(order.getCustomer(), admin, payment, "Transaction 2: Customer to Admin", adminAmount));
        admin.setBalance(admin.getBalance() + adminAmount);
        accountRepository.save(admin);

        for (OrderDetail orderDetail : order.getOrderDetails()) {
            Farm farm = orderDetail.getFarms();
            Account farmHost = farm.getFarmHost();

            if (farmHost != null) {
                double farmHostAmount = paymentAmount * 0.9;
                transactionsSet.add(createTransaction(admin, farmHost, payment, "Transaction 3: Admin to Farm Host", farmHostAmount));

                farmHost.setBalance(farmHost.getBalance() + farmHostAmount);
                accountRepository.save(farmHost);
                break;
            } else {
                throw new PaymentException("Payment failed, Farm host not found!");
            }
        }
        return transactionsSet;

    }

    private void validateOrder(Orders order) {
        if (order == null) {
            throw new NotFoundException("Order not found!");
        }
        if (order.getStatus().equals(OrderStatus.CANCELLED)) {
            throw new ActionException("This order is cancelled, can not process payment!");
        }
    }

    private void validateTour(Tour tour) {
        if (tour == null) {
            throw new NotFoundException("Tour not found!");
        }
    }

    private void validateConsultingStaff(Account consulting, Tour tour) {
        if (consulting == null || !consulting.getRole().equals(Role.CONSULTING)) {
            throw new ActionException("Only consulting staff can perform this action!");
        }
        if (!consulting.getUserId().equals(tour.getConsulting().getUserId())) {
            throw new ActionException("You are not allowed to take action on this tour!");
        }
    }

    private Account getCustomer(String customerId) {
        Account customer = accountRepository.findAccountByUserId(customerId);
        if (customer == null || !customer.getRole().equals(Role.CUSTOMER)) {
            throw new NotFoundException("Customer not found!");
        }
        return customer;
    }

    private void validateCustomerBooking(Tour tour, Account customer) {
        boolean hasBooking = tour.getBookings().stream().anyMatch(booking -> booking.getCustomer().equals(customer));
        if (!hasBooking) {
            throw new NotFoundException("Customer does not have a booking for this tour!");
        }
    }

    private Orders createOrder(OrderRequest orderRequest, Tour tour, Account customer) {
        Orders order = new Orders();
        order.setOrderId(generateOrderId());
        order.setCustomer(customer);
        order.setTour(tour);
        order.setOrderDate(LocalDateTime.now());
        order.setDeliveredDate(null);
        order.setStatus(OrderStatus.PROCESSING);
        order.setNote(orderRequest.getNote());
        order.setPayments(new ArrayList<>());
        ordersRepository.save(order);
        return order;
    }

    private Farm findFarmInTour(Tour tour, String farmId) {
        return tour.getTourSchedules().stream().filter(tourSchedule -> tourSchedule.getFarm()
                .getFarmId()
                .equals(farmId))
                .map(TourSchedule::getFarm)
                .findAny().orElseThrow(() -> new NotFoundException("Farm not found in this tour!"));
    }

    private Koi findKoi(String koiId) {
        Koi koi = koiRepository.findKoiByKoiId(koiId);
        if (koi == null) {
            throw new NotFoundException("Koi not found!");
        }
        return koi;
    }

    private void validateKoiInFarm(Farm farm, Koi koi) {
        if (farm.getKoiFarms().stream().noneMatch(koiFarm -> koiFarm.getKoi().equals(koi))) {
            throw new NotFoundException("This koi species is not in this farm!");
        }
    }

    private List<OrderDetail> createOrderDetails(OrderRequest orderRequest, Tour tour, Orders order) {
        return orderRequest.getOrderDetails().stream().map(orderDetailRequest -> {
            Farm farm = findFarmInTour(tour, orderDetailRequest.getFarmId());
            Koi koi = findKoi(orderDetailRequest.getKoiId());
            validateKoiInFarm(farm, koi);

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrders(order);
            orderDetail.setFarms(farm);
            orderDetail.setKoi(koi);
            orderDetail.setDescription(orderDetailRequest.getDescription());
            orderDetail.setQuantity(orderDetailRequest.getQuantity());
            orderDetail.setPrice(orderDetailRequest.getPrice());

            return orderDetailRepository.save(orderDetail);
        }).collect(Collectors.toList());
    }

    private double calculateTotalPrice(List<OrderDetail> orderDetails) {
        return orderDetails.stream().mapToDouble(OrderDetail::getPrice).sum();
    }

    private OrderResponse convertToOrdersResponse(Orders orders) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(orders.getOrderId());
        orderResponse.setCustomerId(orders.getCustomer().getUserId());
        orderResponse.setTourId(orders.getTour().getTourId());
        orderResponse.setOrderDate(orders.getOrderDate());
        orderResponse.setDeliveredDate(orders.getDeliveredDate());
        orderResponse.setTotalPrice(orders.getTotalPrice());
        orderResponse.setStatus(orders.getStatus());
        orderResponse.setNote(orders.getNote());

        List<OrderDetailResponse> orderDetailResponses = orders.getOrderDetails().stream().map(orderDetail -> {
            OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
            orderDetailResponse.setFarmId(orderDetail.getFarms().getFarmId());
            orderDetailResponse.setKoiId(orderDetail.getKoi().getKoiId());
            orderDetailResponse.setDescription(orderDetail.getDescription());
            orderDetailResponse.setQuantity(orderDetail.getQuantity());
            orderDetailResponse.setPrice(orderDetail.getPrice());

            return orderDetailResponse;
        }).collect(Collectors.toList());

        orderResponse.setOrderDetails(orderDetailResponses);

        return orderResponse;
    }


    private void sendOrderConfirmation(Account account, Orders order) {
        EmailDetail emailDetail = createEmailDetail(account, order, "Xác nhận đơn đặt cá", "https://google.com");
        emailService.sendOrderCompleteEmail(emailDetail);
    }

    private void sendOrderDeliveredSuccessfully(Account account, Orders order) {
        EmailDetail emailDetail = createEmailDetail(account, order, "Xác nhận giao hàng thành công", "https://google.com");
        emailService.sendOrderDeliveredSuccessfully(emailDetail);
    }

    private EmailDetail createEmailDetail(Account account, Orders order, String subject, String link) {
        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setAccount(account);
        emailDetail.setOrder(order);
        emailDetail.setSubject(subject);
        emailDetail.setLink(link);

        return emailDetail;
    }

    private String generateOrderId() {
        return "O" + UUID.randomUUID();
    }
}
