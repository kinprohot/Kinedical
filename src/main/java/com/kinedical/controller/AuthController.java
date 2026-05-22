package com.kinedical.controller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kinedical.dto.AuthRequest;
import com.kinedical.dto.AuthResponse;
import com.kinedical.dto.RegisterRequest;
import com.kinedical.model.User;
import com.kinedical.security.JwtTokenProvider;
import com.kinedical.service.AuditLogService;
import com.kinedical.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final AuditLogService auditLogService;

    public AuthController(AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            UserService userService,
            AuditLogService auditLogService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.auditLogService = auditLogService;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.createToken(authentication);
            User user = userService.findByEmail(request.getEmail())
                    .or(() -> userService.findByUsername(request.getEmail()))
                    .orElseThrow();
            auditLogService.log("LOGIN", user.getId(), "USER", user.getId(), "User logged in successfully.");
            long expiresAt = Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli();
            return ResponseEntity
                    .ok(new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole(), expiresAt));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequest request) {
        try {
            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setEmail(request.getEmail());
            newUser.setPhone(request.getPhone());
            com.kinedical.model.User.Profile profile = new com.kinedical.model.User.Profile();
            profile.setFullName(request.getFullName());
            newUser.setProfile(profile);
            newUser.setRole(User.Role.PATIENT);
            newUser.setStatus(User.Status.ACTIVE);

            User savedUser = userService.register(newUser, request.getPassword());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.createToken(authentication);
            auditLogService.log("REGISTER", savedUser.getId(), "USER", savedUser.getId(),
                    String.format("User %s registered.", savedUser.getEmail()));
            long expiresAt = Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AuthResponse(token, savedUser.getUsername(), savedUser.getEmail(), savedUser.getRole(),
                            expiresAt));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to authenticate new user.");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<User> currentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = authentication.getName();
        User user = userService.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(user);
    }
}
