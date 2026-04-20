package com.vitrung.vizo_dong.service;

import com.vitrung.vizo_dong.entity.User;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.MessageSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
@ConditionalOnClass(name = "org.springframework.mail.javamail.JavaMailSender")
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final MessageSource messageSource;

    @Value("${spring.mail.properties.app.enabled:false}")
    private boolean mailEnabled;

    @Value("${spring.mail.username:}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender,
                        SpringTemplateEngine templateEngine,
                        MessageSource messageSource) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.messageSource = messageSource;
    }

    public void sendWelcomeEmail(User user, Locale locale) throws Exception {
        if (!mailEnabled) {
            log.info("Email đang tắt (spring.mail.properties.app.enabled=false), bỏ qua gửi email cho {}", user.getUsername());
            return;
        }

        Locale effectiveLocale = (locale == null) ? Locale.forLanguageTag("vi") : locale;

        Context context = new Context(effectiveLocale);
        context.setVariable("username", user.getUsername());
        context.setVariable("email", user.getEmail());

        String subject = messageSource.getMessage(
                "mail.welcome.subject",
                new Object[]{user.getUsername()},
                effectiveLocale
        );
        String htmlBody = templateEngine.process("mail/welcome-email", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
        helper.setTo(user.getEmail());
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        if (fromAddress != null && !fromAddress.isBlank()) {
            helper.setFrom(fromAddress);
        }

        mailSender.send(message);
    }
}
