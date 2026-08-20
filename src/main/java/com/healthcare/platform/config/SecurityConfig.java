package com.healthcare.platform.config;

import com.healthcare.platform.repository.UserRepository;
import com.healthcare.platform.security.JwtAuthenticationFilter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/logged-out", "/register",
                                "/faq", "/map", "/api/map/providers",
                                "/api/auth/login", "/api/auth/register", "/api/auth/token",
                                "/css/**", "/img/**", "/images/**", "/js/**", "/error"
                        ).permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Admin user-management panel (Sprint 2 - Nahian Mahmud) - ADMIN only.
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/dashboard").authenticated()
                        .requestMatchers("/api/doctors/search", "/api/hospitals", "/api/pharmacies").hasRole("PATIENT")
                        .requestMatchers("/api/pharmacy/**").hasRole("PHARMACY")
                        .requestMatchers("/pharmacy/**").hasRole("PHARMACY")
                        // Doctor & Patient Module (Sprint 1 - Imtiaz Zaman Sami):
                        // anyone logged in can browse/search doctors; only admins manage records.
                        .requestMatchers(HttpMethod.GET, "/api/doctors/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/doctors/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/doctors/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/doctors/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/patients/**").hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.POST, "/api/patients/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/patients/**").hasAnyRole("ADMIN", "PATIENT")
                        .requestMatchers(HttpMethod.DELETE, "/api/patients/**").hasRole("ADMIN")
                        // Pharmacy Service Module (Sprint 2 - Imtiaz Zaman Sami):
                        // anyone logged in can browse/search medicines; only patients place orders,
                        // and an order can only ever be viewed by its owner (or admin/pharmacy staff),
                        // which OrderService enforces at the data level.
                        .requestMatchers(HttpMethod.GET, "/api/medicines/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/orders").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.GET, "/api/orders/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/pharmacy-store/*/order").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.POST, "/api/notifications/run-reminder-check").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/notifications/run-medicine-reminder-check").hasRole("ADMIN")
                        .requestMatchers("/api/notifications/**").authenticated()
                        .requestMatchers("/notifications/**").authenticated()
                        // Crowdfunding & Payment Module (Sprint 4 - Imtiaz Zaman Sami):
                        // anyone logged in can browse campaigns and donate; only admins
                        // create/edit/close campaigns.
                        .requestMatchers(HttpMethod.GET, "/api/campaigns/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/campaigns/*/donations").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/campaigns/*/close").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/campaigns").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/campaigns/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/donations").authenticated()
                        .requestMatchers(HttpMethod.POST, "/campaigns/*/donate").authenticated()
                        // Health Profile (Sprint 3 - Nahian Mahmud) - PATIENT only. A health
                        // profile (medical history, allergies) belongs to a patient, not a
                        // provider or admin account, so this is restricted the same way
                        // /admin/** is above.
                        .requestMatchers("/health-profile/**", "/api/health/**").hasRole("PATIENT")
                        // Hospital & Diagnostic Module (Member 3): bed/doctor/service
                        // availability is the hospital's own to manage, test offers are the
                        // diagnostic centre's own - same ownership pattern as /health-profile,
                        // and FacilityManagementService double-checks ownership per row too.
                        .requestMatchers("/hospital/**").hasRole("HOSPITAL")
                        .requestMatchers("/diagnostic/**").hasRole("DIAGNOSTIC")
                        // Medical Records Sharing (patient grants/revokes a specific
                        // doctor access to their health profile): the grant/revoke
                        // UI is on the patient's own health profile page (PATIENT-only,
                        // covered by "/health-profile/**" above); this is the doctor's
                        // side - viewing patients who currently have an active grant.
                        .requestMatchers("/doctor/patients/**").hasRole("DOCTOR")
                        // Medicine Reminder: a patient's own reminders, same ownership
                        // pattern as /health-profile.
                        .requestMatchers("/medicine-reminders/**").hasRole("PATIENT")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form
                        .loginPage("/")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureHandler(authenticationFailureHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .permitAll()
                );
        return http.build();
    }

    // Distinguishes "wrong email/password" from "account not approved yet" so
    // the login page can show the right message - a Doctor/Hospital/Pharmacy/
    // Diagnostic/Ambulance account is disabled (is_active = false) until an
    // admin approves it from Admin > Manage Users, and DaoAuthenticationProvider
    // throws DisabledException for those before it ever checks the password.
    @Bean
    AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            String message = exception instanceof DisabledException
                    ? "Your account is pending admin approval. You'll be able to log in once an admin approves it."
                    : "Invalid email or password";
            response.sendRedirect("/?error=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
        };
    }

    @Bean
    UserDetailsService userDetailsService(UserRepository users) {
        return email -> users.findByEmail(email)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPasswordHash())
                        .roles(user.getRole().name())
                        .disabled(!user.isActive())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
