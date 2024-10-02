package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.repository.AccountRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class TokenService {

    @Autowired
    AccountRepository accountRepository;

    // để token của hệ thống là duy nhất => cần một secret key
    public final String SECRET_TOKEN_KEY = "IlikeuiouKioiouioIOuYeutYr465uiY4uuyJioi854jhkgHJG";

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_TOKEN_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // tạo token cho account đăng nhập vô hệ thống
    public String generateToken(Account account) {
        String token = Jwts.builder().subject(account.getUsername() + "")
                .issuedAt(new Date(System.currentTimeMillis())) // lấy thời gian tạo ra token
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSignInKey())
                .compact();
        return token;
    }

    // verify cái token
    public Account getAccountByToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String username = claims.getSubject();
        return accountRepository.findAccountByUsername(username);
    }
}
