package com.grupo3.allslife_framework.framework.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.grupo3.allslife_framework.framework.dto.CreateUserDTO;
import com.grupo3.allslife_framework.framework.dto.NotificationDTO;
import com.grupo3.allslife_framework.framework.dto.UserDTO;
import com.grupo3.allslife_framework.framework.exception.UserNotFoundException;
import com.grupo3.allslife_framework.framework.model.GoalBoard;
import com.grupo3.allslife_framework.framework.model.User;
import com.grupo3.allslife_framework.framework.repository.UserRepository;
import com.grupo3.allslife_framework.framework.security.SecurityUtils;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
// Removida a injeção do SportRoutineService

    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;
    
    @Transactional
    public User create(CreateUserDTO userDTO) {
        if(existsByEmail(userDTO.email())) {
            throw new IllegalArgumentException("Email " + userDTO.email() + " already exists");
        }

        GoalBoard board = new GoalBoard();
        
        User user = new User(
            null, 
            userDTO.name(), 
            userDTO.email(), 
            passwordEncoder.encode(userDTO.password()), 
            board, 
            null,
            null,
            null, 
            null
        );
        
        user = userRepository.save(user);
        
        NotificationDTO notificationDTO = new NotificationDTO(
            "Bem-vindo(a)!", 
            "Sua conta foi criada com sucesso.", 
            user.getId()
        );
        notificationService.create(notificationDTO);
        
        return user;
    }

    public User update(Long id, UserDTO userDTO) {
        User user = getById(id);
        user.setPassword(passwordEncoder.encode(userDTO.password()));
        user.setName(userDTO.name());
        user.setEmail(userDTO.email());
        return userRepository.save(user);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public void deleteById(Long id) {
        Long userLoggedId = securityUtils.getCurrentUserId();
        if (userLoggedId == null) {
            throw new UserNotFoundException("No user logged in");
        }
        User user = getById(id);
        userRepository.delete(user);
    }

    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

}
