package com.grupo3.allslife_app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grupo3.allslife_app.dto.NotificationDTO;
import com.grupo3.allslife_app.exception.SportRoutineNotFoundException;
import com.grupo3.allslife_app.exception.UserNotFoundException;
import com.grupo3.allslife_app.model.Notification;
import com.grupo3.allslife_app.model.SportRoutine;
import com.grupo3.allslife_app.model.User;
import com.grupo3.allslife_app.repository.NotificationRepository;
import com.grupo3.allslife_app.repository.SportRoutineRepository;
import com.grupo3.allslife_app.repository.UserRepository;
import com.grupo3.allslife_app.security.SecurityUtils;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class NotificationService {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SportRoutineRepository sportRoutineRepository;

    @Autowired
    SecurityUtils securityUtils;
    
    @Transactional
    public Notification create(NotificationDTO notificationDTO) {
        User user = userRepository.findById(notificationDTO.receiverId())
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        Notification notification = new Notification(
            null, 
            notificationDTO.title(), 
            notificationDTO.description(), 
            user
        );
        notificationRepository.save(notification);
        return notification;
    }

    public void createRoutineNotification(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        SportRoutine sportRoutine = sportRoutineRepository.findByUserId(userId)
            .orElseThrow(() -> new SportRoutineNotFoundException("Sport Routine not found for UserId: " + userId));
        List<Notification> existingNotifications = notificationRepository.findByReceiver(user);
        if (existingNotifications.stream().anyMatch(n -> n.getDescription().equals(sportRoutine.getGeneratedRoutine() != null ? sportRoutine.getGeneratedRoutine() : "Acesse a sua rotina esportiva para começar a praticar!"))) {
            return;
        }
        Notification notification = new Notification(
            null, 
            "Lembre-se de seguir a sua rotina esportiva, Sr(a) " + user.getName() + "!", 
            sportRoutine.getGeneratedRoutine() != null ? sportRoutine.getGeneratedRoutine() : "Acesse a sua rotina esportiva para começar a praticar!", 
            user
        );
        notificationRepository.save(notification);
    }

    public List<Notification> getAllByReceiver() {
        Long userId = securityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        return notificationRepository.findByReceiver(user);
    }

}
