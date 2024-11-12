package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.exception.*;
import com.project.KoiBookingSystem.model.request.UserRequest;
import com.project.KoiBookingSystem.model.response.UserResponse;
import com.project.KoiBookingSystem.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Transactional
    public UserResponse updateInformation(UserRequest userRequest) {
        Account updatedAccount = authenticationService.getCurrentAccount();
        checkUserNotFound(updatedAccount);
        try {
            if (userRequest.getPhone() != null && !userRequest.getPhone().isEmpty()) {
                updatedAccount.setPhone(userRequest.getPhone());
            }
            if (userRequest.getFullName() != null && !userRequest.getFullName().isEmpty()) {
                updatedAccount.setFullName(userRequest.getFullName());
            }
            if (userRequest.getGender() != null && updatedAccount.getGender() == null) {
                updatedAccount.setGender(userRequest.getGender());
            }
            if (userRequest.getDob() != null && updatedAccount.getDob() == null) {
                updatedAccount.setDob(userRequest.getDob());
            }
            if (userRequest.getAddress() != null && !userRequest.getAddress().isEmpty()) {
                updatedAccount.setAddress(userRequest.getAddress());
            }
            if (userRequest.getNote() != null && !userRequest.getNote().isEmpty()) {
                updatedAccount.setNote(userRequest.getNote());
            }
            Account savedAccount = accountRepository.save(updatedAccount);
            return convertToUserResponse(savedAccount);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains(updatedAccount.getPhone())) {
                throw new DuplicatedException("Số điện thoại đã tồn tại!");
            } else {
                throw new InvalidRequestException(e.getMessage());
            }
        }
    }

    public void deleteAccountRequest() {
        Account currentAccount = authenticationService.getCurrentAccount();
        checkUserNotFound(currentAccount);
        if (currentAccount.getRole() != Role.CUSTOMER) {
            throw new AuthorizationException("Chỉ có khách hàng mới có thể thực hiện hành động này!");
        }
        currentAccount.setDeleteRequestDate(LocalDateTime.now());
        currentAccount.setPendingDeletion(true);
        accountRepository.save(currentAccount);
    }


    @Scheduled(cron = "0 0 0 * * ?") // TASK NÀY SẼ CHẠY VÀO LÚC 0 GIÂY, 0 PHÚT VÀ 0 GIỜ VÀO MỖI NGÀY, MỖI THÁNG, VÀ KHÔNG SET GIÁ TRỊ SỐ NGÀY CHẠY TRONG TUẦN VÌ KHÔNG CẦN THIẾT
    public void processPendingDeletedAccount() {
        List<Account> pendingAccounts = accountRepository.findAccountByPendingDeletionTrue();
        LocalDateTime now = LocalDateTime.now();

        for (Account account : pendingAccounts) {
            if (account.getDeleteRequestDate().plusDays(3).isBefore(now)) {
                account.setStatus(false);
                account.setPendingDeletion(false);
                accountRepository.save(account);
            }
        }
    }

    @Transactional
    public UserResponse deleteUser(String userId) {
        try {
            Account manager = authenticationService.getCurrentAccount();
            checkUserNotFound(manager);
            if (manager.getRole() != Role.MANAGER) {
                throw new AuthorizationException("Chỉ có quản lý mới có thể thực hiện hành động này!");
            }
            Account deletedAccount = accountRepository.findAccountByUserId(userId);
            if (deletedAccount == null || deletedAccount.getRole() == Role.ADMIN || deletedAccount.getRole() == Role.MANAGER) {
                throw new InvalidRequestException("Tài khoản không tồn tại hoặc bạn không có quyền xóa tài khoản này!");
            }
            deletedAccount.setStatus(false);
            accountRepository.save(deletedAccount);
            return convertToUserResponse(deletedAccount);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidRequestException(e.getMessage());
        }
    }


    @Transactional
    public UserResponse unlockUser(String userId) {
        try {
            Account manager = authenticationService.getCurrentAccount();
            checkUserNotFound(manager);
            if (manager.getRole() != Role.MANAGER) {
                throw new AuthorizationException("Chỉ có quản lý mới có thể thực hiện hành động này!");
            }
            Account unlockedAccount = accountRepository.findAccountByUserId(userId);
            if (unlockedAccount == null || unlockedAccount.isStatus()) {
                throw new InvalidRequestException("Tài khoản không tìm thấy hoặc hiện vẫn còn đang hoạt động!");
            }
            unlockedAccount.setStatus(true);
            accountRepository.save(unlockedAccount);
            return convertToUserResponse(unlockedAccount);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidRequestException(e.getMessage());
        }

    }

    public List<UserResponse> getAllUsers() {
        List<Account> accounts = accountRepository.findAccountByStatusTrue();
        if (accounts.isEmpty()) {
            throw new NotFoundException("Danh sách người dùng đang trống!");
        }
        return accounts.stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getAllUsersByRole(Role role) {
        List<Account> accounts = accountRepository.findAccountsByRole(role);
        if (accounts.isEmpty()) {
            throw new NotFoundException("Danh sách người dùng " + role.toString() + " đang trống!");
        }
        return accounts.stream().map(this::convertToUserResponse).collect(Collectors.toList());
    }

    public UserResponse getAccountByUserId(String userId) {
        Account account = accountRepository.findAccountByUserId(userId);
        checkUserNotFound(account);
        return convertToUserResponse(account);
    }

    public UserResponse convertToUserResponse(Account account) {
        UserResponse userResponse = new UserResponse();
        userResponse.setUserID(account.getUserId());
        userResponse.setUsername(account.getUsername());
        userResponse.setPhone(account.getPhone());
        userResponse.setEmail(account.getEmail());
        userResponse.setRole(account.getRole());
        userResponse.setFullName(account.getFullName());
        userResponse.setGender(account.getGender());
        userResponse.setDob(account.getDob());
        userResponse.setAddress(account.getAddress());
        userResponse.setNote(account.getNote());

        return userResponse;
    }

    private void checkUserNotFound(Account account) {
        if (account == null) throw new NotFoundException("Không tìm thấy người dùng!");
        if (!account.isStatus()) throw new InvalidRequestException("Tài khoản này không tồn tại. Để biết thêm thông tin, vui lòng liên hệ hỗ trợ để được giải đáp thắc mắc!");
        if (account.isPendingDeletion()) throw new AuthenticationException("Tài khoản này đang chờ để xóa. Để thực hiện những hành động trên tài khoản này, chủ tài khoản cần đăng nhập lại để hủy quá trình xóa tài khoản!");
    }

}
