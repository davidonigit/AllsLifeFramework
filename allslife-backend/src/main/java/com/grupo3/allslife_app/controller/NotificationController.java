package com.grupo3.allslife_app.controller;

import org.springframework.web.bind.annotation.RestController;

import com.grupo3.allslife_app.model.Notification;
import com.grupo3.allslife_app.service.NotificationService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/notification")
@AllArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(){
        return ResponseEntity.ok(notificationService.getAllByReceiver());
    }
}
