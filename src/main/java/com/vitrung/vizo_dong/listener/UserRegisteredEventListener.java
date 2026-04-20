package com.vitrung.vizo_dong.listener;

import com.vitrung.vizo_dong.event.UserRegisteredEvent;
import com.vitrung.vizo_dong.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(EmailService.class)
public class UserRegisteredEventListener {

    private static final Logger log = LoggerFactory.getLogger(UserRegisteredEventListener.class);

    private final EmailService emailService;

    public UserRegisteredEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async("mailTaskExecutor")
    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        try {
            emailService.sendWelcomeEmail(event.getUser(), event.getLocale());
        } catch (Exception e) {
            log.error("Không thể gửi email chào mừng cho user={}", event.getUser().getUsername(), e);
        }
    }
}
