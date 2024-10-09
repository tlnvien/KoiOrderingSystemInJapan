package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.request.UserRequest;
import com.project.KoiBookingSystem.model.response.UserResponse;
import com.project.KoiBookingSystem.repository.AccountRepository;
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

    @Autowired
    ModelMapper modelMapper;

    public UserResponse updateInformation(UserRequest account) {
        String loggedInByUsername = getLoggedInUsername();
        Account updatedAccount = getAccountByUsername(loggedInByUsername);
        if (account.getFirstName() != null && !account.getFirstName().isEmpty()) {
            updatedAccount.setFirstName(account.getFirstName());
        }
        if (account.getLastName() != null && !account.getLastName().isEmpty()) {
            updatedAccount.setLastName(account.getLastName());
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
        accountRepository.save(updatedAccount);
        return modelMapper.map(updatedAccount, UserResponse.class);
    }

    public UserResponse deleteUser() {
        Account deletedAccount = getAccountByUsername(getLoggedInUsername());
        deletedAccount.setStatus(false);
        accountRepository.save(deletedAccount);
        return modelMapper.map(deletedAccount, UserResponse.class);
    }

    public List<UserResponse> getAllUsers() {
        List<Account> accounts = accountRepository.findAccountByStatusTrue();
        if (accounts.isEmpty()) {
            throw new NotFoundException("List Is Empty!");
        }
        List<UserResponse> users = accounts.stream()
                .map(account -> modelMapper.map(account, UserResponse.class))
                .collect(Collectors.toList());
        return users;
    }

    public List<UserResponse> getAllUsersByRole(String role) {
        List<Account> accounts = accountRepository.findAccountByRole(role);
        if (accounts.isEmpty()) {
            throw new NotFoundException("List is Empty!");
        }
        return accounts.stream().map(account -> modelMapper.map(account, UserResponse.class)).collect(Collectors.toList());
    }

    public Account getAccountByUsername(String username) {
        Account account = accountRepository.findAccountByUsername(username);
        if (account == null) {
            throw new NotFoundException("Account Not Found!");
        }
        return account;
    }

    public String getLoggedInUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }


}
