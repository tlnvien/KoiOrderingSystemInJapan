package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Delivering;
import com.project.KoiBookingSystem.entity.Orders;
import com.project.KoiBookingSystem.enums.DeliveringStatus;
import com.project.KoiBookingSystem.enums.OrderStatus;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.exception.*;
import com.project.KoiBookingSystem.model.request.DeliveringRequest;
import com.project.KoiBookingSystem.model.response.DeliveredOrderResponse;
import com.project.KoiBookingSystem.model.response.DeliveringResponse;
import com.project.KoiBookingSystem.repository.DeliveringRepository;
import com.project.KoiBookingSystem.repository.OrdersRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeliveringService {

    @Autowired
    DeliveringRepository deliveringRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    OrdersRepository ordersRepository;

    @Transactional
    public DeliveringResponse createNewDelivering(DeliveringRequest deliveringRequest) {
        Account deliveringStaff = getDeliveringStaff();

        Delivering delivering = new Delivering();
        delivering.setDeliveringId(generateDeliveringId());
        delivering.setDeliverDate(LocalDateTime.now());
        delivering.setDeliveringStaff(deliveringStaff);
        delivering.setInformation(deliveringRequest.getInformation());
        delivering.setOrders(new ArrayList<>());
        delivering.setStatus(DeliveringStatus.NOT_STARTED);

        Delivering newDelivering = deliveringRepository.save(delivering);

        return convertToDeliveringResponse(newDelivering);
    }

    @Transactional
    public DeliveringResponse addOrderToDelivering(String deliveringId, String orderId) {
        Delivering delivering = deliveringRepository.findByDeliveringId(deliveringId);
        validateDelivering(delivering);
        checkDelivering(delivering);
        Orders order = ordersRepository.findByOrderIdAndExpiredFalse(orderId);
        if (order == null) {
            throw new NotFoundException("Không tìm thấy đơn đặt hàng với Id yêu cầu!");
        }
        if (order.getStatus() != OrderStatus.RECEIVED) {
            throw new InvalidRequestException("Đơn hàng này không thể được thêm vào giỏ hàng vì chưa được nhận bởi nhân viên tư vấn");
        }
        if (order.getDelivering() != null) {
            throw new InvalidRequestException("Đơn hàng này đã được thêm vào một đơn giao hàng khác!");
        }
        order.setDelivering(delivering);
        delivering.getOrders().add(order);

        deliveringRepository.save(delivering);
        ordersRepository.save(order);

        return convertToDeliveringResponse(delivering);
    }


    @Transactional
    public DeliveringResponse removeOrderFromDelivering(String deliveringId, String orderId) {
        Delivering delivering = deliveringRepository.findByDeliveringId(deliveringId);
        validateDelivering(delivering);
        checkDelivering(delivering);
        Orders order = ordersRepository.findByOrderIdAndExpiredFalse(orderId);
        if (order == null) {
            throw new NotFoundException("Không tìm thấy đơn hàng với Id yêu cầu!");
        }
        if (!delivering.getOrders().contains(order)) {
            throw new InvalidRequestException("Đơn đặt hàng không tồn tại trong đơn giao hàng này!");
        }
        delivering.getOrders().remove(order);
        order.setDelivering(null);

        deliveringRepository.save(delivering);
        ordersRepository.save(order);

        return convertToDeliveringResponse(delivering);
    }

    public List<DeliveringResponse> getAllDelivering() {
        List<Delivering> deliveringList = deliveringRepository.findAll();
        if (deliveringList.isEmpty()) {
            throw new EmptyListException("Danh sách đơn giao hàng trống!");
        }
        return deliveringList.stream().map(this::convertToDeliveringResponse).collect(Collectors.toList());
    }


    public List<DeliveringResponse> getAllDeliveringByStatus(DeliveringStatus status) {
        List<Delivering> deliveringList = deliveringRepository.findByStatus(status);
        if (deliveringList.isEmpty()) {
            throw new EmptyListException("Danh sách đơn hàng với trạng thái " + status.toString() + " đang trống!");
        }
        return deliveringList.stream().map(this::convertToDeliveringResponse).collect(Collectors.toList());
    }

    @Transactional
    public DeliveringResponse startDelivering(String deliverId) {
        Delivering delivering = deliveringRepository.findByDeliveringId(deliverId);
        validateDelivering(delivering);
        Account deliveringStaff = getDeliveringStaff();
        if (!deliveringStaff.getUserId().equals(delivering.getDeliveringStaff().getUserId())) {
            throw new InvalidRequestException("Bạn không được phép chỉnh sửa trạng thái của đơn vận chuyển này!");
        }
        if (delivering.getStatus() != DeliveringStatus.NOT_STARTED) {
            throw new InvalidRequestException("Đơn giao hàng này không thể được bắt đầu!");
        }
        delivering.setStatus(DeliveringStatus.STARTING);
        List<Orders> orders = ordersRepository.findByDelivering_DeliveringIdAndExpiredFalse(deliverId);
        for (Orders order : orders) {
            order.setStatus(OrderStatus.DELIVERING);
        }
        ordersRepository.saveAll(orders);

        deliveringRepository.save(delivering);

        return convertToDeliveringResponse(delivering);
    }


    @Transactional
    public DeliveringResponse endDelivering(String deliverId) {
        Delivering delivering = deliveringRepository.findByDeliveringId(deliverId);
        validateDelivering(delivering);
        Account deliveringStaff = getDeliveringStaff();
        if (!deliveringStaff.getUserId().equals(delivering.getDeliveringStaff().getUserId())) {
            throw new InvalidRequestException("Bạn không thể chỉnh sửa trạng thái đơn giao hàng này!");
        }
        if (delivering.getStatus() != DeliveringStatus.STARTING) {
            throw new InvalidRequestException("Đơn hàng này không thể được kết thúc!");
        }
        List<Orders> orders = ordersRepository.findByDelivering_DeliveringIdAndExpiredFalse(deliverId);
        for (Orders order : orders) {
            if (order.getStatus() == OrderStatus.DELIVERING) {
                delivering.getOrders().remove(order);
                order.setDelivering(null);
                order.setStatus(OrderStatus.RECEIVED);
            }
        }
        ordersRepository.saveAll(orders);
        delivering.setStatus(DeliveringStatus.DONE);
        deliveringRepository.save(delivering);

        return convertToDeliveringResponse(delivering);
    }

    private Account getDeliveringStaff() {
        Account delivering = authenticationService.getCurrentAccount();
        if (delivering == null || delivering.getRole() != Role.DELIVERING) {
            throw new AuthorizationException("Chỉ có nhân viên giao hàng mới có thể thực hiện hành động này!");
        }
        return delivering;
    }

    private void validateDelivering(Delivering delivering) {
        if (delivering == null) {
            throw new NotFoundException("Đơn giao hàng không tìm thấy với Id yêu cầu!");
        }
    }

    private DeliveringResponse convertToDeliveringResponse(Delivering delivering) {
        DeliveringResponse deliveringResponse = new DeliveringResponse();
        deliveringResponse.setDeliveringId(delivering.getDeliveringId());
        deliveringResponse.setInformation(delivering.getInformation());
        deliveringResponse.setDeliveringStaffId(delivering.getDeliveringStaff().getUserId());
        deliveringResponse.setDeliverDate(delivering.getDeliverDate());
        deliveringResponse.setStatus(delivering.getStatus());

        List<DeliveredOrderResponse> orderResponses = new ArrayList<>();
        for (Orders order : delivering.getOrders()) {
            DeliveredOrderResponse orderResponse = new DeliveredOrderResponse();
            orderResponse.setOrderId(order.getOrderId());
            orderResponse.setFullName(order.getCustomer().getFullName());
            orderResponse.setPhone(order.getCustomer().getPhone());

            orderResponses.add(orderResponse);
        }

        deliveringResponse.setOrderResponses(orderResponses);

        return deliveringResponse;
    }

    private void checkDelivering(Delivering delivering) {
        if (delivering.getStatus() != DeliveringStatus.NOT_STARTED) {
            throw new InvalidRequestException("Đơn giao hàng này không thể thêm được bất kỳ đơn hàng nào!");
        }
    }

    public String generateDeliveringId() {
        return "D" + UUID.randomUUID();
    }

}
