package com.grupo3.allslife_framework.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grupo3.allslife_framework.dto.NotificationDTO;
import com.grupo3.allslife_framework.exception.RoutineNotFoundException;
import com.grupo3.allslife_framework.exception.UserNotFoundException;
import com.grupo3.allslife_framework.model.AbstractRoutine;
import com.grupo3.allslife_framework.model.Notification;
import com.grupo3.allslife_framework.model.User;
import com.grupo3.allslife_framework.repository.AbstractRoutineRepository;
import com.grupo3.allslife_framework.repository.NotificationRepository;
import com.grupo3.allslife_framework.repository.UserRepository;
import com.grupo3.allslife_framework.security.SecurityUtils;

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
    AbstractRoutineRepository<AbstractRoutine> abstractRoutineRepository;

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
        AbstractRoutine routine = abstractRoutineRepository.findByUserId(userId)
            .orElseThrow(() -> new RoutineNotFoundException("Routine not found for UserId: " + userId));
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
