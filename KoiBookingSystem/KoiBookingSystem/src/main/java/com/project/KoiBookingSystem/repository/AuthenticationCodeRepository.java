package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.AuthenticationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthenticationCodeRepository extends JpaRepository<AuthenticationCode, Long> {

    AuthenticationCode findByCode(String code);

    List<AuthenticationCode> findByAccountAndExpiredFalse(Account account);
}
