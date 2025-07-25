package com.grupo3.allslife_framework.framework.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grupo3.allslife_framework.framework.dto.NotificationDTO;
import com.grupo3.allslife_framework.framework.exception.UserNotFoundException;
import com.grupo3.allslife_framework.framework.model.AbstractRoutine;
import com.grupo3.allslife_framework.framework.model.Notification;
import com.grupo3.allslife_framework.framework.model.User;
import com.grupo3.allslife_framework.framework.repository.NotificationRepository;
import com.grupo3.allslife_framework.framework.repository.UserRepository;
import com.grupo3.allslife_framework.framework.security.SecurityUtils;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityUtils securityUtils;
    
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

    public void createRoutineNotification(Long userId, AbstractRoutine routine) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        List<Notification> existingNotifications = notificationRepository.findByReceiver(user);
        if (existingNotifications.stream().anyMatch(n -> n.getDescription().equals(routine.getGeneratedRoutine() != null ? routine.getGeneratedRoutine() : "Acesse a sua rotina para começar a praticar!"))) {
            return;
        }
        Notification notification = new Notification(
            null, 
            "Lembre-se de seguir a sua rotina, Sr(a) " + user.getName() + "!", 
            routine.getGeneratedRoutine() != null ? routine.getGeneratedRoutine() : "Acesse a sua rotina para começar a praticar!", 
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
