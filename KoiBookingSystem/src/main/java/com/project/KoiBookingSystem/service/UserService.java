package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.exception.DuplicatedException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.request.UserRequest;
import com.project.KoiBookingSystem.model.response.UserResponse;
import com.project.KoiBookingSystem.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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
            throw new DuplicatedException("Duplicated Phone number!");
        }
    }

    @Transactional
    public UserResponse deleteUser() {
        Account deletedAccount = authenticationService.getCurrentAccount();
        checkUserNotFound(deletedAccount);
        deletedAccount.setStatus(false);
        accountRepository.save(deletedAccount);
        return convertToUserResponse(deletedAccount);
    }

    public List<UserResponse> getAllUsers() {
        List<Account> accounts = accountRepository.findAccountByStatusTrue();
        if (accounts.isEmpty()) {
            throw new NotFoundException("List Is Empty!");
        }
        return accounts.stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getAllUsersByRole(Role role) {
        List<Account> accounts = accountRepository.findAccountsByRole(role);
        if (accounts.isEmpty()) {
            throw new NotFoundException("List is Empty!");
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
        if (account == null) throw new NotFoundException("User Not Found!");
    }

}
