package com.ead.notification.services.impl;

import com.ead.notification.repositories.NotificationRepository;
import com.ead.notification.services.NotificationService;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    //Injeção de dependência na raça
    final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository){
        this.notificationRepository = notificationRepository;
    }
}
