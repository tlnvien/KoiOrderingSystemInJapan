package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // lấy Account của người dùng
    Account findAccountByUsername(String username);

    Account findAccountByUserId(String userId);

    long countByRole(Role role);

    List<Account> findAccountByStatusTrue();

    List<Account> findAccountsByRole(Role role);

    Account findAccountByEmail(String email);

    Account findAccountByRole(Role role);

    List<Account> findAccountByPendingDeletionTrue();

}
