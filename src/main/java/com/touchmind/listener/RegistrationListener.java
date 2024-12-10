package com.touchmind.listener;

import com.touchmind.core.event.OnRegistrationCompleteEvent;
import com.touchmind.core.mongo.model.User;
import com.touchmind.core.service.UserService;
import com.touchmind.mail.service.EMail;
import com.touchmind.mail.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    Logger LOG = LoggerFactory.getLogger(RegistrationListener.class);
    @Autowired
    private UserService service;

    @Autowired
    private MessageSource messages;

    @Autowired
    private MailService mailService;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(user, token);

        String recipientAddress = event.getToAddress();
        String subject = event.getSubject();
        String confirmationUrl
                = event.getAppUrl() + "/registrationConfirm?level=" + event.getLevel() + "&token=" + token;

        EMail eMail = new EMail();

        //eMail.setFrom("healthcheck@cheil.com");
        eMail.setTo(recipientAddress);

        String content = event.getContent() + "\r\n" + confirmationUrl;

        eMail.setSubject(subject);
        eMail.setContent(content);
        mailService.sendEmail(eMail);
    }
}
