package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Delivering;
import com.project.KoiBookingSystem.entity.Orders;
import com.project.KoiBookingSystem.enums.DeliveringStatus;
import com.project.KoiBookingSystem.enums.OrderStatus;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.exception.ActionException;
import com.project.KoiBookingSystem.exception.EmptyListException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.request.DeliveringRequest;
import com.project.KoiBookingSystem.model.response.DeliveredOrderResponse;
import com.project.KoiBookingSystem.model.response.DeliveringResponse;
import com.project.KoiBookingSystem.model.response.EmailDetail;
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
        delivering.setStatus(DeliveringStatus.N0T_STARTED);

        Delivering newDelivering = deliveringRepository.save(delivering);

        return convertToDeliveringResponse(newDelivering);

    }


    @Transactional
    public DeliveringResponse addOrderToDelivering(String deliveringId, String orderId) {
        Delivering delivering = deliveringRepository.findByDeliveringId(deliveringId);
        if (delivering == null) {
            throw new NotFoundException("Delivery not found!");
        }
        Orders order = ordersRepository.findByOrderId(orderId);
        if (order == null) {
            throw new NotFoundException("Order not found!");
        }
        if (order.getDelivering() != null) {
            throw new ActionException("Order is already existing in another delivery!");
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
        if (delivering == null) {
            throw new NotFoundException("Delivery not found!");
        }
        Orders order = ordersRepository.findByOrderId(orderId);
        if (order == null) {
            throw new NotFoundException("Order not found!");
        }
        if (!delivering.getOrders().contains(order)) {
            throw new ActionException("Order does not exist in this delivery!");
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
            throw new EmptyListException("Delivering list is empty!");
        }
        return deliveringList.stream().map(this::convertToDeliveringResponse).collect(Collectors.toList());
    }


    public List<DeliveringResponse> getAllDeliveringByStatus(DeliveringStatus status) {
        List<Delivering> deliveringList = deliveringRepository.findByStatus(status);
        if (deliveringList.isEmpty()) {
            throw new EmptyListException("This status delivering list is empty!");
        }
        return deliveringList.stream().map(this::convertToDeliveringResponse).collect(Collectors.toList());
    }

    @Transactional
    public DeliveringResponse startDelivering(String deliverId) {
        Delivering delivering = deliveringRepository.findByDeliveringId(deliverId);
        validateDelivering(delivering);
        Account deliveringStaff = getDeliveringStaff();
        if (!deliveringStaff.getUserId().equals(delivering.getDeliveringStaff().getUserId())) {
            throw new ActionException("You are not allowed to modify this delivery status!");
        }
        if (!delivering.getStatus().equals(DeliveringStatus.N0T_STARTED)) {
            throw new ActionException("This delivery can not be started yet!");
        }
        delivering.setStatus(DeliveringStatus.STARTING);
        deliveringRepository.save(delivering);

        return convertToDeliveringResponse(delivering);
    }


    @Transactional
    public DeliveringResponse endDelivering(String deliverId) {
        Delivering delivering = deliveringRepository.findByDeliveringId(deliverId);
        validateDelivering(delivering);
        Account deliveringStaff = getDeliveringStaff();
        if (!deliveringStaff.getUserId().equals(delivering.getDeliveringStaff().getUserId())) {
            throw new ActionException("You are not allowed to modify this delivery status!");
        }
        if (!delivering.getStatus().equals(DeliveringStatus.STARTING)) {
            throw new ActionException("This delivery can not be ended yet!");
        }
        delivering.setStatus(DeliveringStatus.STARTING);
        deliveringRepository.save(delivering);

        return convertToDeliveringResponse(delivering);
    }

    private Account getDeliveringStaff() {
        Account delivering = authenticationService.getCurrentAccount();
        if (delivering == null || !delivering.getRole().equals(Role.DELIVERING)) {
            throw new ActionException("Only delivering staff can perform this action!");
        }
        return delivering;
    }

    private void validateDelivering(Delivering delivering) {
        if (delivering == null) {
            throw new NotFoundException("Delivery not found!");
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

    public String generateDeliveringId() {
        return "D" + UUID.randomUUID();
    }
}
