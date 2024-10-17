package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.request.UserRequest;
import com.project.KoiBookingSystem.model.response.UserResponse;
import com.project.KoiBookingSystem.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    AccountRepository accountRepository;


    @Transactional
    public UserResponse updateInformation(UserRequest account) {
        String loggedInByUsername = getLoggedInUsername();
        Account updatedAccount = getAccountByUsername(loggedInByUsername);
        if (account.getFullName() != null && !account.getFullName().isEmpty()) {
            updatedAccount.setFullName(account.getFullName());
        }
        if (account.getGender() != null && updatedAccount.getGender() == null) {
            updatedAccount.setGender(account.getGender());
        }
        if (account.getDob() != null && updatedAccount.getDob() == null) {
            updatedAccount.setDob(account.getDob());
        }
        if (account.getAddress() != null && !account.getAddress().isEmpty()) {
            updatedAccount.setAddress(account.getAddress());
        }
        if (account.getNote() != null && !account.getNote().isEmpty()) {
            updatedAccount.setNote(account.getNote());
        }
        Account savedAccount = accountRepository.save(updatedAccount);
        return convertToUserResponse(savedAccount);
    }

    public UserResponse deleteUser() {
        Account deletedAccount = getAccountByUsername(getLoggedInUsername());
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

    public Account getAccountByUsername(String username) {
        Account account = accountRepository.findAccountByUsername(username);
        checkUserNotFound(account);
        return account;
    }

    public String getLoggedInUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
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
