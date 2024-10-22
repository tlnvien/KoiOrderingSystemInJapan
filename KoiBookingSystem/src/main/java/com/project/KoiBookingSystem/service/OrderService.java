package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.*;
import com.project.KoiBookingSystem.enums.*;
import com.project.KoiBookingSystem.exception.ActionException;
import com.project.KoiBookingSystem.exception.EmptyListException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.exception.PaymentException;
import com.project.KoiBookingSystem.model.request.OrderRequest;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    FarmRepository farmRepository;

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
    BookingRepository bookingRepository;

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
        if (tour == null) {
            throw new NotFoundException("Tour not found!");
        }
        Account consulting = authenticationService.getCurrentAccount();
        if (consulting == null || !consulting.getRole().equals(Role.CONSULTING)) {
            throw new ActionException("Only consulting staff can perform this action!");
        }
        if (!tour.getConsulting().equals(consulting)) {
            throw new ActionException("You are not allowed to create order for this tour!");
        }
        Account customer = accountRepository.findAccountByUserId(customerId);
        if (customer == null || !customer.getRole().equals(Role.CUSTOMER)) {
            throw new NotFoundException("Customer not found!");
        }
        for (Booking booking : tour.getBookings()) {
            if (booking.getCustomer().equals(customer)) {
                Orders orders = new Orders();
                orders.setOrderId(generateOrderId());
                orders.setCustomer(customer);
                orders.setTour(tour);
                orders.setOrderDate(LocalDateTime.now());
                orders.setStatus(OrderStatus.PREPARING);
                orders.setNote(orderRequest.getNote());
                orders.setPayment(null);
                orders.setTotalPrice(0);

                Orders newOrder = ordersRepository.save(orders);

                List<OrderDetail> orderDetails = orderRequest.getOrderDetails().stream().map(orderDetailRequest -> {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrders(newOrder);

                    Farm farm = tour.getTourSchedules().stream().filter(tourSchedule -> tourSchedule.getFarm().getFarmId().equals(orderDetailRequest.getFarmId()))
                            .map(TourSchedule::getFarm)
                            .findAny()
                            .orElseThrow(() -> new NotFoundException("Farm in this tour not found!"));

                    Koi koi = koiRepository.findKoiByKoiId(orderDetailRequest.getKoiId());
                    if (koi == null) {
                        throw new NotFoundException("Koi not found!");
                    }
                    if (farm.getKoiFarms().stream().noneMatch(koiFarm -> koiFarm.getKoi().equals(koi) && koiFarm.isStatus())) {
                        throw new ActionException("This koi species is not in this farm!");
                    }

                    orderDetail.setFarms(farm);
                    orderDetail.setKoi(koi);
                    orderDetail.setDescription(orderDetailRequest.getDescription());
                    return orderDetail;

                }).collect(Collectors.toList());

                orderDetailRepository.saveAll(orderDetails);
                double totalPrice = orderDetails.stream().mapToDouble(OrderDetail::getPrice).sum();
                newOrder.setTotalPrice(totalPrice);
                ordersRepository.save(newOrder);

                return convertToOrdersResponse(newOrder);
            }
        }
        throw new ActionException("Customer does not have a booking for this tour!");
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
        if (!status.equals(OrderStatus.PREPARING) && !status.equals(OrderStatus.SENT)) {
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
        if (!order.getStatus().equals(OrderStatus.SENT)) {
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
        if (order.getStatus().equals(OrderStatus.RECEIVED)) {
            throw new ActionException("This order can not be modified yet!");
        }
        if (!status.equals(OrderStatus.DELIVERING) && !status.equals(OrderStatus.DELIVERED) && !status.equals(OrderStatus.CANCELLED)) {
            throw new ActionException("You are not allowed to modify these order status!");
        }
        order.setStatus(status);
        Orders savedOrder = ordersRepository.save(order);
        return convertToOrdersResponse(savedOrder);
    }


    @Transactional
    public void createOrderTransaction(String orderId) {
        Orders order = ordersRepository.findByOrderId(orderId);
        validateOrder(order);
        if (order.getPayment() != null) {
            throw new ActionException("Order can not be paid yet!");
        }
        Payment payment = createOrderPayment(order);
        try {
            Set<Transactions> transactionsSet = createOrderTransactions(payment, orderId);

            payment.setTransactions(transactionsSet);
            payment.setStatus(PaymentStatus.COMPLETED);

            paymentRepository.save(payment);

        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new PaymentException("Payment transaction failed: " + e.getMessage());
        }
    }

    private Payment createOrderPayment(Orders order) {
        Payment payment = new Payment();
        payment.setPaymentId(paymentService.generatePaymentId());
        payment.setOrders(order);
        payment.setBooking(null);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setDescription("Thanh toan bang VNPAY: " + order.getOrderId());
        payment.setPaymentType(PaymentType.ORDER);
        payment.setMethod(PaymentMethod.BANKING);
        payment.setCurrency(PaymentCurrency.VND);

        return payment;
    }

    public String createOrderPaymentUrl(String orderId) {
        Orders order = ordersRepository.findByOrderId(orderId);
        validateOrder(order);
        double amount = order.getTotalPrice();
        try {
            return vnPayService.createPaymentUrl(orderId, amount, "Order");
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new PaymentException("Payment error: " + e.getMessage());
        }

    }

    private Transactions createTransaction(Account fromAccount, Account toAccount, Payment payment, String description) {
        Transactions transaction = new Transactions();
        try {
            transaction.setFromAccount(fromAccount);
            transaction.setToAccount(toAccount);
            transaction.setPayment(payment);
            transaction.setDescription(description);
            transaction.setStatus(TransactionsEnum.SUCCESS);

            return transaction;
        } catch (Exception e) {
            transaction.setStatus(TransactionsEnum.FAILED);
            throw new PaymentException("Transaction initiation failed!");
        }
    }

    private Set<Transactions> createOrderTransactions(Payment payment, String orderId) {
        Set<Transactions> transactionsSet = new HashSet<>();

        Orders order = ordersRepository.findByOrderId(orderId);
        validateOrder(order);
        transactionsSet.add(createTransaction(null, order.getCustomer(), payment, "Transaction 1: VNPay to Customer"));

        Account admin = accountRepository.findAccountByRole(Role.ADMIN);
        if (admin == null || admin.getRole().equals(Role.ADMIN)) {
            throw new NotFoundException("There is no admin account in the system!");
        }


        transactionsSet.add(createTransaction(order.getCustomer(), admin, payment, "Transaction 2: Customer to Admin"));

        double adminBalance = admin.getBalance() + payment.getOrders().getTotalPrice() * 0.1;
        admin.setBalance(adminBalance);
        accountRepository.save(admin);

        for (OrderDetail orderDetail : order.getOrderDetails()) {
            Farm farm = orderDetail.getFarms();
            Account farmHost = farm.getFarmHost();

            if (farmHost != null) {
                transactionsSet.add(createTransaction(admin, farmHost, payment, "Transaction 3: Admin to Farm Host"));

                double farmHostBalance = farmHost.getBalance() + payment.getOrders().getTotalPrice() * 0.9;
                farmHost.setBalance(farmHostBalance);
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
    }

    private OrderResponse convertToOrdersResponse(Orders orders) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(orders.getOrderId());
        orderResponse.setCustomerId(orders.getCustomer().getUserId());
        orderResponse.setTourId(orders.getTour().getTourId());
        orderResponse.setOrderDate(orders.getOrderDate());
        orderResponse.setTotalPrice(orders.getTotalPrice());
        orderResponse.setStatus(orders.getStatus());
        orderResponse.setNote(orders.getNote());

        List<OrderDetailResponse> orderDetailResponses = orders.getOrderDetails().stream().map(orderDetail -> {
            OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
            orderDetailResponse.setFarmId(orderDetail.getFarms().getFarmId());
            orderDetailResponse.setKoiId(orderDetail.getKoi().getKoiId());
            orderDetailResponse.setQuantity(orderDetail.getQuantity());
            orderDetailResponse.setPrice(orderDetail.getPrice());

            return orderDetailResponse;
        }).collect(Collectors.toList());

        orderResponse.setOrderDetails(orderDetailResponses);

        return orderResponse;
    }

    private String generateOrderId() {
        return UUID.randomUUID().toString();
    }
}
