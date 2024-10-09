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
<<<<<<< HEAD
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
=======
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

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

<<<<<<< HEAD
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // Add your frontend URL here
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // Allow credentials (cookies, authorization headers, etc.)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply CORS configuration to all endpoints
        return source;
    }

=======
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

    // định nghĩa cho Spring Security biết đâu là authenticationService và lớp Filter của project
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
<<<<<<< HEAD
        httpSecurity
=======
        return httpSecurity
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
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
<<<<<<< HEAD
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
=======
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class).build();
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
    }

}
