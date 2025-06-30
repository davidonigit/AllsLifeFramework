package com.grupo3.allslife_app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.grupo3.allslife_app.dto.CreateUserDTO;
import com.grupo3.allslife_app.dto.NotificationDTO;
import com.grupo3.allslife_app.dto.UserDTO;
import com.grupo3.allslife_app.exception.UserNotFoundException;
import com.grupo3.allslife_app.model.GoalBoard;
import com.grupo3.allslife_app.model.SportRoutine;
import com.grupo3.allslife_app.model.User;
import com.grupo3.allslife_app.repository.UserRepository;
import com.grupo3.allslife_app.security.SecurityUtils;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    SportRoutineService sportRoutineService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    SecurityUtils securityUtils;
    
    @Transactional
    public User create(CreateUserDTO userDTO) {
        if(existsByEmail(userDTO.email())) {
            throw new IllegalArgumentException("Email " + userDTO.email() + " already exists");
        }
        GoalBoard board = new GoalBoard();
        SportRoutine sportRoutine = new SportRoutine();
        User user = new User(null, userDTO.name(), userDTO.email(), passwordEncoder.encode(userDTO.password()), board, sportRoutine, null, null);
        sportRoutine.setUser(user);
        user = userRepository.save(user);
        sportRoutineService.initializeWeeklyAvailability(sportRoutine.getId());
        NotificationDTO notificationDTO = new NotificationDTO("Bem vindo(a)", "Seja muito bem vindo(a), ao SportsLife", user.getId());
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

}
