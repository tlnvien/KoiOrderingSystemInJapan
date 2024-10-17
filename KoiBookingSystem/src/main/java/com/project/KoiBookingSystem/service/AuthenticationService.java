package com.project.KoiBookingSystem.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.AuthorizationCode;
import com.project.KoiBookingSystem.enums.AuthorizationType;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.exception.ActionException;
import com.project.KoiBookingSystem.exception.AuthenticationException;
import com.project.KoiBookingSystem.exception.DuplicatedException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.request.*;
import com.project.KoiBookingSystem.model.response.EmailDetail;
import com.project.KoiBookingSystem.model.response.LoginResponse;
import com.project.KoiBookingSystem.model.response.RegisterResponse;
import com.project.KoiBookingSystem.repository.AccountRepository;
import com.project.KoiBookingSystem.repository.AuthorizationCodeRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.channels.ScatteringByteChannel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    TokenService tokenService;

    @Autowired
    EmailService emailService;

    @Autowired
    AuthorizationCodeRepository authorizationCodeRepository;


    // CÁC HÀM XỬ LÍ ĐẶC TRƯNG

    @Transactional
    public RegisterResponse registerAccount(RegisterRequest registerRequest) {
        return registerAccount(registerRequest, Role.CUSTOMER);
    }


    @Transactional
    public RegisterResponse registerManagerAccount(RegisterRequest registerRequest) {
        return registerAccount(registerRequest, Role.MANAGER);
    }


    @Transactional
    public RegisterResponse registerStaffAccount(RegisterRequest registerRequest, Role role) {
        return registerAccount(registerRequest, role);
    }


    private RegisterResponse registerAccount(RegisterRequest registerRequest, Role role) {
        Account account = modelMapper.map(registerRequest, Account.class);
        encodePasswordAndCreateDefaults(account, role);
        try {

            Account newAccount = accountRepository.save(account);
            String registrationCode = generateAuthorizationCode();
            saveAuthorizationCode(newAccount, registrationCode, AuthorizationType.REGISTER);

            sendRegisterCode(newAccount, registrationCode);
            return modelMapper.map(newAccount, RegisterResponse.class);
        } catch (DataIntegrityViolationException e) {
            handleDuplicateData(e, account);
        }
        throw new AuthenticationException("Registration failed!");
    }


    public LoginResponse loginAccount(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            ));
            Account account = (Account) authentication.getPrincipal();
            if (!account.isConfirmed()) {
                expirePreviousAuthorizationCode(account);

                String newCode = generateAuthorizationCode();
                saveAuthorizationCode(account, newCode, AuthorizationType.REGISTER);
                sendRegisterCode(account, newCode);

                throw new AuthenticationException("Your account email was not confirmed. A new email was sent. Please confirm your account email to access the system!");
            }
            LoginResponse loginResponse = modelMapper.map(account, LoginResponse.class);
            loginResponse.setToken(tokenService.generateToken(account));
            return loginResponse;
        } catch (UsernameNotFoundException e) {
            throw new AuthenticationException("Username not Found!");
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Invalid Username or Password!");
        } catch (DisabledException e) {
            throw new AuthenticationException("Your account is disabled!");
        }
    }


    public String authenticationFirebase(String token) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);

            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();
            String fullName = decodedToken.getName();
            boolean emailVerified = decodedToken.isEmailVerified();

            Account account = accountRepository.findAccountByEmail(email);
            if (account == null) {
                account = new Account();
                account.setEmail(email);
                account.setUsername(uid);
                account.setFullName(fullName);
                account.setUserId(generateUserID(Role.CUSTOMER));
                account.setRole(Role.CUSTOMER);
                account.setCreatedDate(LocalDate.now());
                account.setConfirmed(emailVerified);

                accountRepository.save(account);
            }

            accountRepository.save(account);

            return email;   
        } catch (FirebaseAuthException e) {
            throw new AuthenticationException("Error login using Firebase Authentication!");
        }
    }


    public void forgotAccountPassword(ForgotPasswordRequest request) {
        Account forgotAccount = accountRepository.findAccountByEmail(request.getEmail());
        if (forgotAccount == null) throw new NotFoundException("Account with the provided Email Not Found!");
        if (!forgotAccount.isStatus()) throw new ActionException("Account with this Email is not available!");
        expirePreviousAuthorizationCode(forgotAccount);
        String code = generateAuthorizationCode();
        saveAuthorizationCode(forgotAccount, code, AuthorizationType.RESET_PASSWORD);
        sendForgotPasswordEmail(forgotAccount, code);
    }



    // SEND MAIL LOGIC

    private void sendWelcomeEmail(Account account) {
        EmailDetail emailDetail = createEmailDetail(account, "Chào mừng đến với Koi Booking System", "https://google.com/", null);
        emailService.sendWelcomeEmail(emailDetail);
    }


    private void sendForgotPasswordEmail(Account account, String code) {
        String resetLink = "https://google.com/reset-password?token=" + code;
        EmailDetail emailDetail = createEmailDetail(account, "Yêu cầu nhập lại mật khẩu", resetLink, code);
        emailService.sendForgotPasswordEmail(emailDetail);
    }


    private EmailDetail createEmailDetail(Account account, String subject, String link, String code) {
        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setAccount(account);
        emailDetail.setSubject(subject);
        emailDetail.setLink(link);
        emailDetail.setCode(code);
        return emailDetail;
    }


    // AUTHORIZATION CODE LOGIC

    private void expirePreviousAuthorizationCode(Account account) {
        List<AuthorizationCode> authorizationCodes = authorizationCodeRepository.findByAccountAndExpiredFalse(account);

        for (AuthorizationCode code : authorizationCodes) {
            code.setExpired(true);
            code.setExpirationDate(LocalDateTime.now());
        }

        authorizationCodeRepository.saveAll(authorizationCodes);
    }


    private String generateAuthorizationCode() {
        return String.format("%06d",(int) (Math.random()* 1000000));
    }


    public void resetAccountPassword(ResetPasswordRequest request, String requestCode, String email) {
        AuthorizationCode code = checkValidCode(requestCode, email);

        Account account = code.getAccount();
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        accountRepository.save(account);

        code.setExpired(true);
        authorizationCodeRepository.save(code);
    }


    private void sendRegisterCode(Account account, String code) {

        EmailDetail emailDetail = createEmailDetail(account, "Xác nhận email đăng ký!", null + code, code);
        emailService.sendRegistrationCodeEmail(emailDetail);
    }


    public void completeRegistration(String code, String email) {
        AuthorizationCode authorizationCode = checkValidCode(code, email);
        Account account = authorizationCode.getAccount();
        account.setConfirmed(true);
        accountRepository.save(account);

        authorizationCode.setExpired(true);
        authorizationCodeRepository.save(authorizationCode);

        sendWelcomeEmail(account);
    }


    private void saveAuthorizationCode(Account account, String code, AuthorizationType type) {
        AuthorizationCode authorizationCode = new AuthorizationCode();
        authorizationCode.setCode(code);
        authorizationCode.setAccount(account);
        authorizationCode.setType(type);
        authorizationCode.setExpired(false);
        authorizationCode.setExpirationDate(LocalDateTime.now().plusHours(1));

        authorizationCodeRepository.save(authorizationCode);
    }

    private AuthorizationCode checkValidCode(String code, String email) {
        AuthorizationCode authorizationCode = authorizationCodeRepository.findByCode(code);
        if (authorizationCode == null) {
            throw new AuthenticationException("The provided code is invalid!");
        }
        if (authorizationCode.isExpired()) {
            throw new AuthenticationException("The provided code is expired!");
        }
        Account account = authorizationCode.getAccount();
        if (!account.getEmail().equals(email)) {
            throw new AuthenticationException("The provided code is not associated with your email!");
        }

        return authorizationCode;
    }


    // CÁC HÀM LIÊN QUAN ĐẾN XỬ LÝ LOGIC AUTHENTICATION

    private void encodePasswordAndCreateDefaults(Account account, Role role) {
        String passwordString = account.getPassword();
        account.setPassword(passwordEncoder.encode(passwordString));
        account.setRole(role);
        account.setUserId(generateUserID(role));
        account.setCreatedDate(LocalDate.now());
        account.setStatus(true);
        account.setConfirmed(false);
    }

    private void handleDuplicateData(DataIntegrityViolationException e, Account account) {
        if (e.getMessage().contains(account.getUsername())) {
            throw new DuplicatedException("Duplicated Username!");
        } else if (e.getMessage().contains(account.getPhone())) {
            throw new DuplicatedException("Duplicated Phone number!");
        } else {
            throw new DuplicatedException("Duplicated Email!");
        }
    }

    public String generateUserID(Role role) {
        String prefix = "";
        switch (role) {
            case CUSTOMER:
                prefix = "CU";
                break;
            case SALES:
                prefix = "SS";
                break;
            case CONSULTING:
                prefix = "CS";
                break;
            case DELIVERING:
                prefix = "DS";
                break;
            case MANAGER:
                prefix = "MG";
                break;
            case FARM_HOST:
                prefix = "FH";
                break;
            case ADMIN:
                prefix = "AD";
                break;
            default:
                throw new IllegalArgumentException("Invalid role " + role);
        }
        long countRole = accountRepository.countByRole(role);
        return prefix + (countRole + 1);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails =  accountRepository.findAccountByUsername(username);
        if (userDetails == null) throw new UsernameNotFoundException("Username not found: " + username);
        return userDetails;
    }

    public Account getCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Account) authentication.getPrincipal();
    }
}
