package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Request;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.exception.AuthenticationException;
import com.project.KoiBookingSystem.exception.AuthorizationException;
import com.project.KoiBookingSystem.exception.EmptyListException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.request.FarmHostRequest;
import com.project.KoiBookingSystem.model.response.FarmHostResponse;
import com.project.KoiBookingSystem.repository.RequestRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestService {

    @Autowired
    RequestRepository requestRepository;

    @Autowired
    AuthenticationService authenticationService;


    @Transactional
    public FarmHostResponse createNewRequest(FarmHostRequest farmHostRequest) {
        Account farmHost = authenticationService.getCurrentAccount();
        if (farmHost == null || !farmHost.getRole().equals(Role.FARM_HOST)) {
            throw new AuthorizationException("Chỉ có chủ trang trại mới thực hiện được hành động này!");
        }
        checkUserIsPendingDeletion(farmHost);
        Request request = new Request();
        request.setRequestId(generateRequestId());
        request.setInformation(farmHostRequest.getInformation());
        request.setFarmHost(farmHost);
        request.setCreatedDate(LocalDate.now());
        request.setDone(false);

        requestRepository.save(request);

        return convertToFarmHostResponse(request);
    }

    public List<FarmHostResponse> getAllFarmHostRequest() {
        List<Request> requests = requestRepository.findAll();
        if (requests.isEmpty()) {
            throw new EmptyListException("Danh sách yêu cầu đang trống!");
        }
        return requests.stream().map(this::convertToFarmHostResponse).collect(Collectors.toList());
    }

    public List<FarmHostResponse> getAllRequestNotDone() {
        List<Request> requests = requestRepository.findByDoneFalse();
        if (requests.isEmpty()) {
            throw new EmptyListException("Danh sách yêu cầu đang trống!");
        }
        return requests.stream().map(this::convertToFarmHostResponse).collect(Collectors.toList());
    }

    public List<FarmHostResponse> getAllRequestDone() {
        List<Request> requests = requestRepository.findByDoneTrue();
        if (requests.isEmpty()) {
            throw new EmptyListException("Danh sách yêu cầu đang trống!");
        }
        return requests.stream().map(this::convertToFarmHostResponse).collect(Collectors.toList());
    }


    public List<FarmHostResponse> getAllRequestByFarmHost(String farmHostId) {
        List<Request> requests = requestRepository.findByFarmHost_UserId(farmHostId);
        if (requests.isEmpty()) {
            throw new EmptyListException("Danh sách yêu cầu đang trống!");
        }
        return requests.stream().map(this::convertToFarmHostResponse).collect(Collectors.toList());
    }


    @Transactional
    public FarmHostResponse markCompletedRequest(String requestId) {
        Request request = requestRepository.findByRequestId(requestId);
        if (request == null) {
            throw new NotFoundException("Không tìm thấy yêu cầu với Id cung cấp!");
        }
        request.setDone(true);
        request.setCompletedDate(LocalDate.now());

        Request savedRequest = requestRepository.save(request);
        return convertToFarmHostResponse(savedRequest);
    }


    private FarmHostResponse convertToFarmHostResponse(Request request) {
        FarmHostResponse farmHostResponse = new FarmHostResponse();
        farmHostResponse.setRequestId(request.getRequestId());
        farmHostResponse.setFarmHostId(request.getFarmHost().getUserId());
        farmHostResponse.setInformation(request.getInformation());
        farmHostResponse.setCreatedDate(request.getCreatedDate());
        farmHostResponse.setDone(request.isDone());

        return farmHostResponse;
    }

    private String generateRequestId() {
        Request lastRequest = requestRepository.findTopByOrderByIdDesc();
        int lastId = 0;
        if (lastRequest != null && lastRequest.getRequestId() != null) {
            String lastRequestId = lastRequest.getRequestId();
            lastId = Integer.parseInt(lastRequestId.substring(2));
        }

        return "RQ" + (lastId + 1);
    }

    private void checkUserIsPendingDeletion(Account account) {
        if (account.isPendingDeletion())
            throw new AuthenticationException("Tài khoản " + account.getUserId() + " đang yêu cầu xóa và không thể thực hiện bất kỳ hành động nào trong hệ thống. Để hủy quá trình này, vui lòng đăng nhập lại!");
        if (!account.isStatus()) throw new AuthenticationException("Tài khoản này đã không còn tồn tại!");
    }
}
