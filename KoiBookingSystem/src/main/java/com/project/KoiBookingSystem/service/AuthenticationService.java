package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.AuthenticationCode;
import com.project.KoiBookingSystem.enums.AuthorizationType;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.exception.AuthenticationException;
import com.project.KoiBookingSystem.exception.AuthorizationException;
import com.project.KoiBookingSystem.exception.DuplicatedException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.request.*;
import com.project.KoiBookingSystem.model.response.EmailDetail;
import com.project.KoiBookingSystem.model.response.LoginResponse;
import com.project.KoiBookingSystem.model.response.RegisterResponse;
import com.project.KoiBookingSystem.repository.AccountRepository;
import com.project.KoiBookingSystem.repository.AuthenticationCodeRepository;
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

import javax.annotation.PostConstruct;
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
    AuthenticationCodeRepository authenticationCodeRepository;


    // CÁC HÀM XỬ LÍ ĐẶC TRƯNG

    @Transactional
    public RegisterResponse registerCustomerAccount(RegisterRequest registerRequest) {
        return registerAccount(registerRequest);
    }


    @Transactional
    public RegisterResponse registerManagerAccount(RegisterStaffRequest registerStaffRequest) {
        return registerStaff(registerStaffRequest, Role.MANAGER);
    }

    @Transactional
    public RegisterResponse registerStaffAccount(RegisterStaffRequest registerStaffRequest, Role role) {
        if (role == Role.CUSTOMER || role == Role.ADMIN || role == Role.MANAGER) {
            throw new AuthorizationException("Quản lý không được phép tạo tài khoản khách hàng, tài khoản quản lý và tài khoản quản trị viên!");
        }
        return registerStaff(registerStaffRequest, role);
    }

    @PostConstruct // HÀM SẼ CHẠY KHI APP KHỞI ĐỘNG
    public void initWhenStarted() {
        createAdminAccount();
    }

    private void createAdminAccount() { // TẠO TÀI KHOẢN ADMIN TỰ ĐỘNG
        String username = "administrator";
        Account adminAccount = accountRepository.findAccountByUsername(username);
        if (adminAccount == null) {
            Account newAdmin = new Account();
            newAdmin.setUsername(username);
            newAdmin.setPassword(passwordEncoder.encode("administrator"));
            newAdmin.setRole(Role.ADMIN);
            newAdmin.setEmail("admin@mail.com");
            newAdmin.setStatus(true);
            newAdmin.setConfirmed(true);
            newAdmin.setBalance(0);
            newAdmin.setCreatedDate(LocalDate.now());
            newAdmin.setDeleteRequestDate(null);
            newAdmin.setPendingDeletion(false);
            newAdmin.setUserId(generateUserID(Role.ADMIN));

            accountRepository.save(newAdmin);
        }
    }


    private RegisterResponse registerAccount(RegisterRequest registerRequest) {
        Account account = modelMapper.map(registerRequest, Account.class);
        encodePasswordAndCreateDefaults(account);
        try {

            Account newAccount = accountRepository.save(account);

            String registrationCode = generateAuthenticationCode();
            saveAuthenticationCode(newAccount, registrationCode, AuthorizationType.REGISTER);

            sendRegisterCode(newAccount, registrationCode);

            return modelMapper.map(newAccount, RegisterResponse.class);
        } catch (DataIntegrityViolationException e) {
            handleDuplicateData(e, account);
        }
        throw new AuthenticationException("Đăng ký tài khoản thất bại!");
    }


    private RegisterResponse registerStaff(RegisterStaffRequest request, Role role) {
        if (role == Role.ADMIN) {
            Account admin = accountRepository.findAccountByRole(Role.ADMIN);
            if (admin != null) {
                throw new AuthenticationException("Tài khoản admin đã được đăng ký! Không thể đăng ký thêm cho tài khoản admin!");
            }
        }
         Account account = new Account();
         try {
             account.setUserId(generateUserID(role));
             account.setUsername(request.getUsername());
             account.setPassword(passwordEncoder.encode(request.getPassword()));
             account.setPhone(request.getPhone());
             account.setEmail(request.getEmail());
             account.setFullName(request.getFullName());
             account.setGender(request.getGender());
             account.setDob(request.getDob());
             account.setRole(role);
             account.setCreatedDate(LocalDate.now());
             account.setStatus(true);
             account.setDeleteRequestDate(null);
             account.setPendingDeletion(false);
             account.setBalance(0);
             account.setConfirmed(true);

             accountRepository.save(account);

             return modelMapper.map(account, RegisterResponse.class);
         } catch (DataIntegrityViolationException e) {
             handleDuplicateData(e, account);
         }
         throw new AuthenticationException("Đăng ký tài khoản cho nhân viên thất bại!");
    }


    public LoginResponse loginAccount(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            ));
            Account account = (Account) authentication.getPrincipal();
            if (!account.isConfirmed()) {
                expirePreviousAuthenticationCode(account);

                String newCode = generateAuthenticationCode();
                saveAuthenticationCode(account, newCode, AuthorizationType.REGISTER);
                sendRegisterCode(account, newCode);

                throw new AuthenticationException("Tài khoản chưa được xác thực. Một thư xác nhận mới được gửi đến tài khoản email của bạn. Vui lòng kiểm tra!");
            }
            if (account.isPendingDeletion()) {
                account.setPendingDeletion(false);
                accountRepository.save(account);
                throw new AuthenticationException("Tài khoản của bạn đã được hủy yêu cầu xóa. Vui lòng đăng nhập lại để truy cập vào hệ thống!");
            }
            LoginResponse loginResponse = modelMapper.map(account, LoginResponse.class);
            loginResponse.setToken(tokenService.generateToken(account));
            return loginResponse;
        } catch (UsernameNotFoundException e) {
            throw new AuthenticationException("Tên đăng nhập không tồn tại!");
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Tên đăng nhập hoặc mật khẩu không hợp lệ!");
        } catch (DisabledException e) {
            throw new AuthenticationException("Tài khoản này đã bị vô hiệu hóa! Liên hệ hỗ trợ để biết thêm thông tin chi tiết.");
        }
    }




    public void forgotAccountPassword(ForgotPasswordRequest request) {
        Account forgotAccount = accountRepository.findAccountByEmail(request.getEmail());
        if (forgotAccount == null) {
            throw new NotFoundException("Không tìm thấy tài khoản với email mà bạn cung cấp!");
        }
        if (!forgotAccount.isStatus()) {
            throw new AuthenticationException("Tài khoản liên kết với email này đã bị vô hiệu hóa! Không thể thực hiện bất cứ hành động nào với tài khoản này!");
        }
        expirePreviousAuthenticationCode(forgotAccount);
        String code = generateAuthenticationCode();
        saveAuthenticationCode(forgotAccount, code, AuthorizationType.RESET_PASSWORD);
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


    // AUTHENTICATION CODE LOGIC

    private void expirePreviousAuthenticationCode(Account account) {
        List<AuthenticationCode> authenticationCodes = authenticationCodeRepository.findByAccountAndExpiredFalse(account);

        for (AuthenticationCode code : authenticationCodes) {
            code.setExpired(true);
            code.setExpirationDate(LocalDateTime.now());
        }

        authenticationCodeRepository.saveAll(authenticationCodes);
    }


    private String generateAuthenticationCode() {
        return String.format("%06d",(int) (Math.random()* 1000000));
    }


    public void resetAccountPassword(ResetPasswordRequest request, String requestCode, String email) {
        AuthenticationCode code = checkValidCode(requestCode, email);

        Account account = code.getAccount();
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        accountRepository.save(account);

        code.setExpired(true);
        authenticationCodeRepository.save(code);
    }


    private void sendRegisterCode(Account account, String code) {
        EmailDetail emailDetail = createEmailDetail(account, "Xác nhận email đăng ký!", null + code, code);
        emailService.sendRegistrationCodeEmail(emailDetail);
    }


    public void completeRegistration(String code, String email) {
        AuthenticationCode authenticationCode = checkValidCode(code, email);
        Account account = authenticationCode.getAccount();
        account.setConfirmed(true);
        accountRepository.save(account);

        authenticationCode.setExpired(true);
        authenticationCodeRepository.save(authenticationCode);

        sendWelcomeEmail(account);
    }


    private void saveAuthenticationCode(Account account, String code, AuthorizationType type) {
        AuthenticationCode authenticationCode = new AuthenticationCode();
        authenticationCode.setCode(code);
        authenticationCode.setAccount(account);
        authenticationCode.setType(type);
        authenticationCode.setExpired(false);
        authenticationCode.setExpirationDate(LocalDateTime.now().plusHours(1));

        authenticationCodeRepository.save(authenticationCode);
    }

    private AuthenticationCode checkValidCode(String code, String email) {
        AuthenticationCode authenticationCode = authenticationCodeRepository.findByCode(code);
        if (authenticationCode == null) {
            throw new AuthenticationException("Mã kích hoạt không hợp lệ!");
        }
        if (authenticationCode.isExpired()) {
            throw new AuthenticationException("Mã kích hoạt đã hết hiệu lực!");
        }
        Account account = authenticationCode.getAccount();
        if (!account.getEmail().equals(email)) {
            throw new AuthenticationException("Mã kích hoạt này không hợp lệ với mã mà bạn đã nhận được!");
        }

        return authenticationCode;
    }


    // CÁC HÀM LIÊN QUAN ĐẾN XỬ LÝ LOGIC AUTHENTICATION

    private void encodePasswordAndCreateDefaults(Account account) {
        String passwordString = account.getPassword();
        account.setPassword(passwordEncoder.encode(passwordString));
        account.setRole(Role.CUSTOMER);
        account.setUserId(generateUserID(Role.CUSTOMER));
        account.setCreatedDate(LocalDate.now());
        account.setStatus(true);
        account.setDeleteRequestDate(null);
        account.setPendingDeletion(false);
        account.setBalance(0);
        account.setConfirmed(false);
    }

    private void handleDuplicateData(DataIntegrityViolationException e, Account account) {
        if (e.getMessage().contains(account.getUsername())) {
            throw new DuplicatedException("Tên đăng nhập đã tồn tại trong hệ thống!");
        } else if (e.getMessage().contains(account.getPhone())) {
            throw new DuplicatedException("Số điện thoại này đã được đăng ký trong một tài khoản khác!");
        } else {
            throw new DuplicatedException("Email này đã được đăng ký ở một tài khoản khác!");
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
                throw new IllegalArgumentException("Role không hợp lệ: " + role);
        }
        long countRole = accountRepository.countByRole(role);
        return prefix + (countRole + 1);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails =  accountRepository.findAccountByUsername(username);
        if (userDetails == null) throw new UsernameNotFoundException("Tên đăng nhập không tồn tại: " + username);
        return userDetails;
    }

    public Account getCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Account) authentication.getPrincipal();
    }


}
