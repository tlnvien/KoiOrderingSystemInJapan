package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Request;
import com.project.KoiBookingSystem.enums.RequestStatus;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.exception.AuthenticationException;
import com.project.KoiBookingSystem.exception.EmptyListException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.request.RequestRequest;
import com.project.KoiBookingSystem.model.response.RequestResponse;
import com.project.KoiBookingSystem.repository.RequestRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestService {

    @Autowired
    RequestRepository requestRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    ModelMapper modelMapper;

    public RequestResponse createRequest(RequestRequest requestRequest) {
        Account customer = authenticationService.getCurrentAccount();
        if (!customer.getRole().equals(Role.CUSTOMER)) {
            throw new AuthenticationException("Invalid Activity!");
        }
        Request request = new Request();
        request.setRequestId(generateRequestId());
        if (customer.getFirstName() == null || customer.getFirstName().isEmpty()) {
            request.setFirstName(requestRequest.getFirstName());
        }
        if (customer.getLastName() == null || customer.getLastName().isEmpty()) {
            request.setLastName(requestRequest.getLastName());
        }
        if (customer.getPhone() == null || customer.getPhone().isEmpty()) {
            request.setPhone(requestRequest.getPhone());
        }
        request.setNumberOfParticipants(requestRequest.getNumberOfParticipants());
        request.setTextBox(requestRequest.getTextBox());
        request.setVisaCheck(requestRequest.isVisaCheck());
        request.setCreatedDate(LocalDateTime.now());
        request.setCustomer(customer);
        request.setStatus(RequestStatus.NOT_TAKEN);

        Request newRequest = requestRepository.save(request);

        modelMapper.typeMap(Request.class, RequestResponse.class).addMappings(mapper -> { mapper.map(src -> src.getCustomer().getUserId(), RequestResponse::setUserId);
        mapper.map(src -> src.getSales() != null ? src.getSales().getUserId() : null, RequestResponse::setSalesId);});

        return modelMapper.map(newRequest, RequestResponse.class);
    }

    public void takeRequest(String requestId) {
        Account sales = authenticationService.getCurrentAccount();
        if (!sales.getRole().equals(Role.SALES)) {
            throw new AuthenticationException("Invalid Activity!");
        }
        Request request = requestRepository.findByRequestIdAndStatus(requestId, RequestStatus.NOT_TAKEN);
        if (request == null) throw new NotFoundException("Request Not Found!");

        request.setSales(sales);
        request.setStatus(RequestStatus.IN_PROGRESS);
        requestRepository.save(request);
    }

    public List<RequestResponse> getAllRequests() {
        List<Request> requests = requestRepository.findAll();
        checkList(requests);
        return requests.stream().map(request -> modelMapper.map(request, RequestResponse.class)).collect(Collectors.toList());
    }

    public List<RequestResponse> searchRequests(String requestId, String customerId, RequestStatus status) {
        List<Request> requests = requestRepository.searchRequests(requestId, customerId, status);
        checkList(requests);
        return requests.stream().map(request -> modelMapper.map(request, RequestResponse.class)).collect(Collectors.toList());
    }

    public void completeRequest(String requestId) {
        Account sales = authenticationService.getCurrentAccount();
        if (!sales.getRole().equals(Role.SALES)) {
            throw new AuthenticationException("Invalid Activity!");
        }
        Request request = requestRepository.findByRequestIdAndStatus(requestId, RequestStatus.IN_PROGRESS);
        if (request == null) throw new NotFoundException("Request Not Found!");
        if (request.getSales().getUserId().equals(sales.getUserId())) {
            throw new AuthenticationException("Action Denied!");
        }
        request.setStatus(RequestStatus.COMPLETED);
        requestRepository.save(request);
    }

    public String generateRequestId() {
        Request lastRequest = requestRepository.findTopByOrderByIdDesc();
        int lastId = 0;
        if (lastRequest != null && lastRequest.getRequestId() != null) {
            String lastRequestId = lastRequest.getRequestId();
            lastId = Integer.parseInt(lastRequestId.substring(1));
        }

        return "R" + (lastId + 1);
    }

    public void checkList(List<Request> requests) {
        if (requests.isEmpty()) throw new EmptyListException("No Request Found!");
    }
}
