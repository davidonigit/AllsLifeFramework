package com.grupo3.allslife_app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupo3.allslife_app.dto.LoginRequestDTO;
import com.grupo3.allslife_app.dto.LoginResponseDTO;
import com.grupo3.allslife_app.dto.UserDTO;
import com.grupo3.allslife_app.model.User;
import com.grupo3.allslife_app.repository.UserRepository;
import com.grupo3.allslife_app.security.TokenService;
import com.grupo3.allslife_app.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final NotificationService notificationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO body) {
        User user = this.userRepository.findByEmail(body.email())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (passwordEncoder.matches(body.password(), user.getPassword())) {
            String token = this.tokenService.generateToken(user);
            notificationService.createRoutineNotification(user.getId());
            return ResponseEntity.ok(new LoginResponseDTO(user.getEmail(), token,
                    new UserDTO(user.getName(), user.getEmail(), null, user.getId())));
        }
        return ResponseEntity.badRequest().build();
    }
}
