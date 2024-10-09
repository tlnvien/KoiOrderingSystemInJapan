package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // lấy Account của người dùng
    Account findAccountByUsername(String username);

<<<<<<< HEAD
    Account findAccountByUserId(String userId);
=======
    Account findAccountByUserID(String userID);
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

    long countByRole(Role role);

    List<Account> findAccountByStatusTrue();
<<<<<<< HEAD

    List<Account> findAccountByRole(String role);
=======
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
}
