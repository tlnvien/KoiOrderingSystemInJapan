package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.AuthorizationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorizationCodeRepository extends JpaRepository<AuthorizationCode, Long> {

    AuthorizationCode findByCode(String code);

    List<AuthorizationCode> findByAccountAndExpiredFalse(Account account);
}
