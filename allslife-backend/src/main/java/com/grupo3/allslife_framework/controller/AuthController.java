package com.grupo3.allslife_framework.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupo3.allslife_framework.dto.LoginRequestDTO;
import com.grupo3.allslife_framework.dto.LoginResponseDTO;
import com.grupo3.allslife_framework.dto.UserDTO;
import com.grupo3.allslife_framework.model.User;
import com.grupo3.allslife_framework.repository.UserRepository;
import com.grupo3.allslife_framework.security.TokenService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO body) {
        User user = this.userRepository.findByEmail(body.email())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (passwordEncoder.matches(body.password(), user.getPassword())) {
            String token = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new LoginResponseDTO(user.getEmail(), token,
                    new UserDTO(user.getName(), user.getEmail(), null, user.getId())));
        }
        return ResponseEntity.badRequest().build();
    }
}
