package com.project.KoiBookingSystem.config;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.exception.AuthenticationException;
import com.project.KoiBookingSystem.service.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component // đánh dấu đây như là một bean và có thể autowired ra nhiều class khác
// Filter này sẽ chạy một lần mỗi khi người dùng đăng nhập vào hệ thống
public class Filter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    HandlerExceptionResolver handlerExceptionResolver; // trả về những lỗi trong class này


    // List các API public mà tất cả mọi người đều có thể truy cập
    private final List<String> AUTH_PERMISSION = List.of(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/api/register",
            "/api/register/confirm/**",
            "/api/login",
            "/api//forgot-password",
            "/api//reset-password",
            "/api/koi/list",
            "/api/koi/search",
            "/api/koi/details/*",
            "/api/farm/details/*",
            "/api/koi/images",
            "/api/tour/history",
            "/api/koi/images/*",
            "/api/farm/list",
            "/api/farm/search",
            "/api/farm/images/*",
            "/api/tour/schedule/all/**",
            "/api/koiFarm/listKoi/**",
            "/api/koiFarm/listFarm/**",
            "/api/tour/search/**",
            "/api/payment/initiate",
            "/api/feedback/tour/*",
            "/api/feedback/all",
            "/api/feedback/tour/*",
            "/api/feedback/negative",
            "/api/feedback/positive"
    );

    public boolean checkIsPublicAPI(String uri) {
        // Nếu gặp các API trong list ở trên thì cho phép truy cập
        AntPathMatcher pathMatcher = new AntPathMatcher(); // nhờ check giúp cái pattern và cái uri
        // Nếu không, phân quyền (check Token)
        return AUTH_PERMISSION.stream().anyMatch(pattern -> pathMatcher.match(pattern, uri)); // xem có thằng nào match, nếu có thì trả về true, không thì trả về false
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // check xem API mà người dùng yêu cầu có phải là một public API hay không?
        boolean isPublicAPI = checkIsPublicAPI(request.getRequestURI());
        if (isPublicAPI) {
            filterChain.doFilter(request, response); // cho phép request có thể truy cập vào các controller (api)
        } else {
            String token = getToken(request);
            if (token == null) {
                // không có token => không được phép truy cập
                handlerExceptionResolver.resolveException(request, response, null, new AuthenticationException("Mã định danh không tồn tại!"));
                return;
            }
            // check xem token có đúng không?
            // lấy thông tin account từ token gửi đến
            Account account;
            try {
                account = tokenService.getAccountByToken(token);
            } catch (ExpiredJwtException expiredJwtException) {
                // token hết hạn
                handlerExceptionResolver.resolveException(request, response, null, new AuthenticationException("Mã định danh đã hết hạn!"));
                return;
            } catch (MalformedJwtException malformedJwtException) {
                // Token không hợp lệ, token ma quỷ thần thánh nào đó
                handlerExceptionResolver.resolveException(request, response, null, new AuthenticationException("Mã định danh không hợp lệ!"));
                return;
            }

            // Token hợp lệ => cho phép truy cập
            // Luu lại thông tin của account
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(account,
                    token,
                    account.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            // Cho phép truy cập
            filterChain.doFilter(request, response);
        }
    }


    // lấy token từ yêu cầu
    public String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            return null;
        }
        return authHeader.substring(7); // vì API được đưa về từ backend có thêm ký tự Bearer ở đằng trước
        // => mình không cần đến cái ký tự đó => bỏ qua và lấy index từ số 7
    }
}
