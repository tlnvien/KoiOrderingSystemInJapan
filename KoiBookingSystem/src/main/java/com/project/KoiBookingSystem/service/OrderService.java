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
import org.springframework.scheduling.annotation.Scheduled;
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
        if (tour.getStatus() != TourStatus.IN_PROGRESS) {
            throw new InvalidRequestException("Tour này chưa được bắt đầu, đơn đặt hàng không được phép tạo!");
        }

        Account consulting = authenticationService.getCurrentAccount();
        validateConsultingStaff(consulting, tour);
        Account customer = getCustomer(customerId);
        validateCustomerBooking(tour, customer);
        if (customer.getAddress() == null || customer.getAddress().isEmpty()) {
            if (orderRequest.getCustomerAddress() != null && !orderRequest.getCustomerAddress().isEmpty()) {
                customer.setAddress(orderRequest.getCustomerAddress());
            } else {
                throw new InvalidRequestException("Hãy nhập địa chỉ của khách hàng để có thể đặt đơn hàng!");
            }
        }
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
        List<Orders> orders = ordersRepository.findByCustomer_UserIdAndExpiredFalse(customerId);
        if (orders.isEmpty()) {
            throw new EmptyListException("Danh sách đơn đặt hàng đang trống!");
        }
        return orders.stream().map(this::convertToOrdersResponse).collect(Collectors.toList());
    }


    public List<OrderResponse> getAllOrdersByTour(String tourId) {
        List<Orders> orders = ordersRepository.findByTour_TourIdAndExpiredFalse(tourId);
        if (orders.isEmpty()) {
            throw new EmptyListException("Danh sách đơn đặt hàng đang trống!");
        }
        return orders.stream().map(this::convertToOrdersResponse).collect(Collectors.toList());
    }


    public List<OrderResponse> getAllOrderReceived() {
        List<Orders> orders = ordersRepository.findByStatusReceived();
        if (orders.isEmpty()) {
            throw new EmptyListException("Danh sách đơn hàng mà nhân viên tư vấn đã nhận trong hệ thống đang trống!");
        }
        return orders.stream().map(this::convertToOrdersResponse).collect(Collectors.toList());
    }


    public List<OrderResponse> getAllOrdersByFarm(String farmId) {
        List<Orders> orders = ordersRepository.findByFarm_FarmId(farmId);
        if (orders.isEmpty()) {
            throw new EmptyListException("Danh sách đơn hàng trên trang trại này đang trống!");
        }
        return orders.stream().map(this::convertToOrdersResponse).collect(Collectors.toList());
    }


    public List<OrderResponse> getAllOrdersByFarmHost(String farmId) {
        Account farmHost = authenticationService.getCurrentAccount();
        if (farmHost == null || farmHost.getRole() != Role.FARM_HOST) {
            throw new AuthorizationException("Chỉ có chủ trang trại mới có thể thực hiện hành động này!");
        }
        Farm farm = farmRepository.findFarmByFarmIdAndStatusTrue(farmId);
        if (!farmHost.getUserId().equals(farm.getFarmHost().getUserId())) {
            throw new InvalidRequestException("Bạn không phải là chủ trang trại của trang trại này!");
        }
        List<Orders> orders = ordersRepository.findByFarm_FarmId(farm.getFarmId());
        if (orders.isEmpty()) {
            throw new EmptyListException("Danh sách đơn hàng được yêu cầu của trang trại này đang trống");
        }
        return orders.stream().map(this::convertToOrdersResponse).collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse updateOrderStatusByFarmHost(String orderId, OrderStatus status) {
        Orders order = ordersRepository.findByOrderIdAndExpiredFalse(orderId);
        validateOrder(order);
        boolean firstPaymentCompleted = order.getPayments().stream()
                .anyMatch(ordersPayment -> ordersPayment.getStatus() == OrderPaymentStatus.FIRST_PAYMENT && ordersPayment.getPayment().getStatus() == PaymentStatus.COMPLETED);

        if (!firstPaymentCompleted) {
            throw new InvalidRequestException("Thanh toán cho lần đầu tiên của đơn hàng chưa thành công, chưa thể cập nhật trạng thái đơn hàng!");
        }
        Account farmHost = authenticationService.getCurrentAccount();
        if (farmHost == null || !farmHost.getRole().equals(Role.FARM_HOST)) {
            throw new InvalidRequestException("Trạng thái đơn đặt hàng hiện tại chỉ có thể được cập nhật bởi chủ trang trại!");
        }
        if (!farmHost.getUserId().equals(order.getFarms().getFarmHost().getUserId())) {
            throw new InvalidRequestException("Đơn hàng không thể được cập nhật vì bạn không phải là chủ trang trại của đơn hàng này!");
        }
        if (!status.equals(OrderStatus.PREPARING) && !status.equals(OrderStatus.SHIPPING)) {
            throw new InvalidRequestException("Bạn không thể cập nhật trạng thái đơn hàng bằng những trạng thái này!");
        }
        order.setStatus(status);
        Orders savedOrder = ordersRepository.save(order);
        return convertToOrdersResponse(savedOrder);
    }


    @Transactional
    public OrderResponse updateOrderStatusByConsulting(String orderId, OrderStatus status) {
        Orders order = ordersRepository.findByOrderIdAndExpiredFalse(orderId);
        validateOrder(order);
        Account consulting = authenticationService.getCurrentAccount();
        if (consulting == null || !consulting.getRole().equals(Role.CONSULTING)) {
            throw new InvalidRequestException("Trạng thái đơn hàng hiện tại chỉ có thể được cập nhật bởi nhân viên tư vấn!");
        }
        if (!order.getTour().getConsulting().getUserId().equals(consulting.getUserId())) {
            throw new InvalidRequestException("Bạn không thể cập nhật cho đơn hàng này vì bạn không phải là nhân viên tư vấn của tour!");
        }
        if (!order.getStatus().equals(OrderStatus.SHIPPING)) {
            throw new InvalidRequestException("Đơn hàng hiện tại chưa thể được cập nhật bởi nhân viên tư vấn!");
        }
        if (!status.equals(OrderStatus.RECEIVED)) {
            throw new InvalidRequestException("Bạn không thể cập nhật trạng thái này cho đơn hàng!");
        }
        order.setStatus(status);
        Orders savedOrder = ordersRepository.save(order);
        return convertToOrdersResponse(savedOrder);
    }


    @Transactional
    public OrderResponse updateOrderStatusByDelivering(String orderId, OrderStatus status) {
        Orders order = ordersRepository.findByOrderIdAndExpiredFalse(orderId);
        validateOrder(order);
        Account delivering = authenticationService.getCurrentAccount();
        if (delivering == null || !delivering.getRole().equals(Role.DELIVERING)) {
            throw new AuthorizationException("Trạng thái hiện tại của đơn hàng chỉ có thể được cập nhật bởi nhân viên giao hàng!");
        }
        if (!order.getStatus().equals(OrderStatus.RECEIVED) && !order.getStatus().equals(OrderStatus.DELIVERING)) {
            throw new InvalidRequestException("Bạn chưa thể cập nhật được đơn hàng này!");
        }
        if (!status.equals(OrderStatus.CANCELLED)) {
            throw new InvalidRequestException("Bạn không có quyền cập nhật đơn hàng bằng những trạng thái này!");
        }
        order.setStatus(status);
        Orders savedOrder = ordersRepository.save(order);
        return convertToOrdersResponse(savedOrder);
    }


    @Transactional
    public void createFirstOrderTransaction(String orderId) {
        Orders order = ordersRepository.findByOrderIdAndExpiredFalse(orderId);
        validateOrder(order);
        boolean firstPaymentCompleted = order.getPayments().stream()
                .anyMatch(ordersPayment -> ordersPayment.getStatus() == OrderPaymentStatus.FIRST_PAYMENT && ordersPayment.getPayment().getStatus() == PaymentStatus.COMPLETED);

        if (firstPaymentCompleted) {
            throw new InvalidRequestException("Thanh toán cho lần đầu tiên của đơn hàng đã thành công, không thể thanh toán lại!");
        }

        boolean finalPaymentCompleted = order.getPayments()
                .stream()
                .anyMatch(ordersPayment -> ordersPayment.getStatus() == OrderPaymentStatus.FINAL_PAYMENT && ordersPayment.getPayment().getStatus() == PaymentStatus.COMPLETED);

        if (finalPaymentCompleted) {
            throw new InvalidRequestException("Thanh toán cuối cùng của đơn hàng này đã tìm thấy, không thể thanh toán lần đầu tiên cho đơn hàng này!");
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
            throw new PaymentException("Giao dịch cho thanh toán đơn hàng lần đầu thất bại: " + e.getMessage());
        }
    }


    @Transactional
    public void createFinalOrderTransaction(String orderId) {
        Orders order = ordersRepository.findByOrderIdAndExpiredFalse(orderId);
        validateOrder(order);

        if (order.getStatus() != OrderStatus.DELIVERING) {
            throw new InvalidRequestException("Thanh toán này chỉ có thể được thực hiện khi đơn hàng đang ở trạng thái đang vận chuyển!");
        }
        boolean anyCompletedFirstPayment = order.getPayments().stream()
                .filter(ordersPayment -> ordersPayment.getStatus() == OrderPaymentStatus.FIRST_PAYMENT)
                .map(OrdersPayment::getPayment)
                .anyMatch(payment -> payment.getStatus() == PaymentStatus.COMPLETED);

        if (!anyCompletedFirstPayment) {
            throw new InvalidRequestException("Thanh toán cho lần đầu tiên của đơn hàng chưa thành công, không thể thanh toán cho lần cuối!");
        }

        boolean finalPaymentCompleted = order.getPayments().stream()
                .anyMatch(ordersPayment -> ordersPayment.getStatus() == OrderPaymentStatus.FINAL_PAYMENT && ordersPayment.getPayment().getStatus() == PaymentStatus.COMPLETED);

        if (finalPaymentCompleted) {
            throw new InvalidRequestException("Thanh toán cuối cùng của đơn hàng này đã tìm thấy, không thể thanh toán tiếp tục cho đơn hàng này!");
        }
        OrdersPayment finalOrdersPayment = createOrderPayment(order, OrderPaymentStatus.FINAL_PAYMENT);
        Payment finalPayment = finalOrdersPayment.getPayment();

        try {
            Set<Transactions> transactionsSet = createOrderTransactions(finalPayment, orderId, OrderPaymentStatus.FINAL_PAYMENT);

            finalPayment.setTransactions(transactionsSet);
            finalPayment.setStatus(PaymentStatus.COMPLETED);

            order.getPayments().add(finalOrdersPayment);

            order.setDeliveredDate(LocalDateTime.now());
            order.setStatus(OrderStatus.DELIVERED);
            ordersRepository.save(order);

            sendOrderDeliveredSuccessfully(order.getCustomer(), order);
        } catch (Exception e) {
            finalPayment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(finalPayment);
            throw new PaymentException("Thanh toán đơn hàng cho lần cuối thất bại: " + e.getMessage());
        }
    }


    @Transactional
    public OrderResponse updateExpectedDeliveredDate(String orderId, DeliveredDateRequest deliveredDateRequest) {
        Orders order = ordersRepository.findByOrderIdAndExpiredFalse(orderId);
        validateOrder(order);
        if (order.getPayments().isEmpty()) {
            throw new InvalidRequestException("Đơn hàng này chưa được thanh toán!");
        }
        boolean firstPaymentCompleted = order.getPayments().stream()
                .anyMatch(ordersPayment -> ordersPayment.getStatus() == OrderPaymentStatus.FIRST_PAYMENT && ordersPayment.getPayment().getStatus() == PaymentStatus.COMPLETED);

        if (!firstPaymentCompleted) {
            throw new InvalidRequestException("Thanh toán cho lần đầu tiên của đơn hàng chưa thành công, không thể cập nhật ngày giao cho đơn hàng!");
        }
        Account consulting = authenticationService.getCurrentAccount();
        validateConsultingStaff(consulting, order.getTour());

        order.setDeliveredDate(deliveredDateRequest.getDeliveredDate());
        Orders savedOrder = ordersRepository.save(order);

        return convertToOrdersResponse(savedOrder);
    }


    @Transactional
    public void handleCashFirstPayment(String orderId, PaymentCurrency currency) {
        Orders order = ordersRepository.findByOrderIdAndExpiredFalse(orderId);
        validateOrder(order);

        Account consulting = authenticationService.getCurrentAccount();
        validateConsultingStaff(consulting, order.getTour());

        boolean firstPaymentCompleted = order.getPayments().stream()
                .anyMatch(ordersPayment -> ordersPayment.getStatus() == OrderPaymentStatus.FIRST_PAYMENT && ordersPayment.getPayment().getStatus() == PaymentStatus.COMPLETED);

        if (firstPaymentCompleted) {
            throw new InvalidRequestException("Thanh toán cho lần đầu tiên của đơn hàng đã thành công, không thể thanh toán lại!");
        }

        boolean finalPaymentCompleted = order.getPayments()
                .stream()
                .anyMatch(ordersPayment -> ordersPayment.getStatus() == OrderPaymentStatus.FINAL_PAYMENT && ordersPayment.getPayment().getStatus() == PaymentStatus.COMPLETED);

        if (finalPaymentCompleted) {
            throw new InvalidRequestException("Thanh toán cuối cùng của đơn hàng này đã tìm thấy, không thể thanh toán lần đầu tiên cho đơn hàng này!");
        }

        Payment payment = new Payment();
        payment.setPaymentId(paymentService.generatePaymentId());
        payment.setBooking(null);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setDescription("Thanh toán bằng tiền mặt");
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
            throw new PaymentException("Thanh toán bằng tiền mặt cho đơn hàng đầu tiên thất bại: " + e.getMessage());
        }
        order.setStatus(OrderStatus.PREPARING);
        ordersRepository.save(order);
    }

    @Transactional
    public void handleCashFinalPayment(String orderId, PaymentCurrency currency) {
        Orders order = ordersRepository.findByOrderIdAndExpiredFalse(orderId);
        validateOrder(order);

        Account consulting = authenticationService.getCurrentAccount();
        validateConsultingStaff(consulting, order.getTour());

        boolean anyCompletedFirstPayment = order.getPayments().stream()
                .filter(ordersPayment -> ordersPayment.getStatus() == OrderPaymentStatus.FIRST_PAYMENT)
                .map(OrdersPayment::getPayment)
                .anyMatch(payment -> payment.getStatus() == PaymentStatus.COMPLETED);

        if (!anyCompletedFirstPayment) {
            throw new InvalidRequestException("Thanh toán cho lần đầu tiên của đơn hàng chưa thành công, không thể thanh toán cho lần cuối!");
        }

        boolean finalPaymentCompleted = order.getPayments()
                .stream()
                .anyMatch(ordersPayment -> ordersPayment.getStatus() == OrderPaymentStatus.FINAL_PAYMENT && ordersPayment.getPayment().getStatus() == PaymentStatus.COMPLETED);

        if (finalPaymentCompleted) {
            throw new InvalidRequestException("Thanh toán cuối cùng của đơn hàng này đã tìm thấy, không thể thanh toán tiếp tục cho đơn hàng này!");
        }

        Payment payment = new Payment();
        payment.setPaymentId(paymentService.generatePaymentId());
        payment.setBooking(null);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setDescription("Thanh toán bằng tiền mặt");
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
            throw new PaymentException("Thanh toán đơn hàng cho lần cuối cùng thất bại: " + e.getMessage());
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
        payment.setDescription("Thanh toán bằng VNPAY: " + order.getOrderId());
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

    public String createOrderPaymentUrl(String orderId, OrderPaymentStatus status) {
        Orders order = ordersRepository.findByOrderIdAndExpiredFalse(orderId);
        validateOrder(order);
        double amount = 0;
        if (status == OrderPaymentStatus.FINAL_PAYMENT) {
            amount = order.getRemainingPrice();
        } else {
            amount = order.getPaidPrice();
        }
        try {
            return vnPayService.createPaymentUrl(orderId, amount, "Order");
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new PaymentException("Thanh toán thất bại: " + e.getMessage());
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
            throw new PaymentException("Quá trình giao dịch thất bại! " + e.getMessage());
        }
    }

    private Set<Transactions> createOrderTransactions(Payment payment, String orderId, OrderPaymentStatus status) {
        Set<Transactions> transactionsSet = new HashSet<>();
        Orders order = ordersRepository.findByOrderIdAndExpiredFalse(orderId);
        validateOrder(order);

        double paymentAmount;
        if (status == OrderPaymentStatus.FINAL_PAYMENT) {
            paymentAmount = order.getRemainingPrice();
        } else {
            paymentAmount = order.getPaidPrice();
        }

        Account admin = accountRepository.findAccountByRole(Role.ADMIN);
        if (admin == null || !admin.getRole().equals(Role.ADMIN)) {
            throw new NotFoundException("Không có tài khoản admin trong hệ thống!");
        }

        double adminAmount = paymentAmount * 0.1;
        transactionsSet.add(createTransaction(null, order.getCustomer(), payment, "Transaction 1: VNPay to Customer", 0));


        transactionsSet.add(createTransaction(order.getCustomer(), admin, payment, "Transaction 2: Customer to Admin", adminAmount));
        admin.setBalance(admin.getBalance() + adminAmount);
        accountRepository.save(admin);

        Farm farm = order.getFarms();
        Account farmHost = farm.getFarmHost();

        if (farmHost != null) {
            double farmHostAmount = paymentAmount * 0.9;
            transactionsSet.add(createTransaction(admin, farmHost, payment, "Transaction 3: Admin to Farm Host", farmHostAmount));

            farmHost.setBalance(farmHost.getBalance() + farmHostAmount);
            accountRepository.save(farmHost);
        } else {
            throw new PaymentException("Thanh toán thất bại, không tìm thấy chủ trang trại!");
        }

        return transactionsSet;

    }

    private void validateOrder(Orders order) {
        if (order == null) {
            throw new NotFoundException("Không tìm thấy đơn hàng với Id cung cấp!");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidRequestException("Đơn hàng này đã bị hủy, không thể được thực hiện bởi hành động nào nữa!");
        }
    }

    private void validateTour(Tour tour) {
        if (tour == null) {
            throw new NotFoundException("Không tìm thấy tour trong hệ thống!");
        }
        if (tour.getStatus() == TourStatus.CANCELLED) {
            throw new InvalidRequestException("Tour đã bị hủy và không thể được thực hiện bởi bất kỳ hành động gì!");
        }
    }

    private void validateConsultingStaff(Account consulting, Tour tour) {
        if (consulting == null || !consulting.getRole().equals(Role.CONSULTING)) {
            throw new AuthorizationException("Chỉ có nhân viên tư vấn được thực hiện hành động này!");
        }
        if (!consulting.getUserId().equals(tour.getConsulting().getUserId())) {
            throw new InvalidRequestException("Bạn không thể thực hiện hành động trên tour này vì bạn không phải là nhân viên tư vấn của tour!");
        }
    }

    private Account getCustomer(String customerId) {
        Account customer = accountRepository.findAccountByUserId(customerId);
        if (customer == null || customer.getRole() != Role.CUSTOMER) {
            throw new NotFoundException("Khách hàng không tìm thấy!");
        }
        checkUserIsPendingDeletion(customer);
        return customer;
    }

    private void validateCustomerBooking(Tour tour, Account customer) {
        boolean hasValidBooking = tour.getBookings().stream().anyMatch(booking -> booking.getCustomer().equals(customer) && booking.getBookingStatus() == BookingStatus.CHECKED);
        if (!hasValidBooking) {
            throw new NotFoundException("Khách hàng không có trong danh sách đi tour!");
        }
    }

    private Orders createOrder(OrderRequest orderRequest, Tour tour, Account customer) {
        Orders order = new Orders();
        order.setOrderId(generateOrderId());
        order.setCustomer(customer);
        order.setTour(tour);
        order.setFarms(findFarmInTour(tour, orderRequest.getFarmId()));
        order.setOrderDate(LocalDateTime.now());
        order.setDeliveredDate(null);
        order.setStatus(OrderStatus.PROCESSING);
        order.setNote(orderRequest.getNote());
        order.setExpired(false);
        order.setPayments(new ArrayList<>());
        ordersRepository.save(order);
        return order;
    }

    private Farm findFarmInTour(Tour tour, String farmId) {
        return tour.getTourSchedules().stream().filter(tourSchedule -> tourSchedule.getFarm()
                .getFarmId()
                .equals(farmId))
                .map(TourSchedule::getFarm)
                .findAny().orElseThrow(() -> new NotFoundException("Trang trại không tìm thấy trong danh sách lịch trình của tour!"));
    }

    private Koi findKoi(String koiId) {
        Koi koi = koiRepository.findKoiByKoiIdAndStatusTrue(koiId);
        if (koi == null) {
            throw new NotFoundException("Không tìm thấy giống cá trong hệ thống!");
        }
        return koi;
    }

    private void validateKoiInFarm(Farm farm, Koi koi) {
        if (farm.getKoiFarms().stream().noneMatch(koiFarm -> koiFarm.getKoi().equals(koi))) {
            throw new NotFoundException("Giống cá này không có trong trang trại!");
        }
    }

    private List<OrderDetail> createOrderDetails(OrderRequest orderRequest, Tour tour, Orders order) {
        return orderRequest.getOrderDetails().stream().map(orderDetailRequest -> {
            Koi koi = findKoi(orderDetailRequest.getKoiId());
            validateKoiInFarm(order.getFarms(), koi);

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrders(order);
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
        orderResponse.setFarmId(orders.getFarms().getFarmId());
        orderResponse.setOrderDate(orders.getOrderDate());
        orderResponse.setDeliveredDate(orders.getDeliveredDate());
        orderResponse.setTotalPrice(orders.getTotalPrice());
        orderResponse.setCustomerAddress(orders.getCustomer().getAddress());
        orderResponse.setStatus(orders.getStatus());
        orderResponse.setNote(orders.getNote());

        List<OrderDetailResponse> orderDetailResponses = orders.getOrderDetails().stream().map(orderDetail -> {
            OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
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

    private void checkUserIsPendingDeletion(Account account) {
        if (account.isPendingDeletion())
            throw new AuthenticationException("Tài khoản " + account.getUserId() + " đang yêu cầu xóa và không thể thực hiện bất kỳ hành động nào trong hệ thống. Để hủy quá trình này, vui lòng đăng nhập lại!");
        if (!account.isStatus()) throw new AuthenticationException("Tài khoản này đã không còn tồn tại!");
    }


    @Scheduled(fixedRate = 3600000) // 1 GIỜ = 3 TRIỆU 6 MILLISECONDS
    public void expiredUnpaidOrders() {
        LocalDateTime now = LocalDateTime.now();
        List<Orders> unpaidOrders = ordersRepository.findUnpaidOrders(OrderStatus.PROCESSING); // đơn hàng đang chờ được thanh toán
        for (Orders order : unpaidOrders) {
            if (order.getOrderDate().plusHours(1).isBefore(now)) {
                order.setExpired(true);
                order.setStatus(OrderStatus.CANCELLED);
                ordersRepository.save(order);
            }
        }
    }

}
