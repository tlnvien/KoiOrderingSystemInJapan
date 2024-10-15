package com.project.KoiBookingSystem.config;

import com.project.KoiBookingSystem.service.AuthenticationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    Filter filter;

    // Mã hóa password
    @Bean // đánh dấu thư viện để sử dụng cho các class khác
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // map kiểu dữ liệu
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    // decode password trong database để kiểm tra thông tin người dùng khi login có đúng hay không?
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }


    // định nghĩa cho Spring Security biết đâu là authenticationService và lớp Filter của project
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        request -> request
                                .requestMatchers("/**")
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .userDetailsService(authenticationService)
                .sessionManagement(
                        session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class).build();
    }

}
