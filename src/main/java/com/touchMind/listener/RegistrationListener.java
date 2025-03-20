package com.touchMind.listener;

import com.touchMind.core.event.OnRegistrationCompleteEvent;
import com.touchMind.core.mongo.model.User;
import com.touchMind.core.service.UserService;
import com.touchMind.mail.service.EMail;
import com.touchMind.mail.service.MailService;
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

        //eMail.setFrom("healthcheck@touchMind.com");
        eMail.setTo(recipientAddress);

        String href = "<a href='" + confirmationUrl + "' target='_blank'>Approve</a>";
        if (event.getLevel().equalsIgnoreCase("3")) {
            href = "<a href='" + confirmationUrl + "' target='_blank'>Verify</a>";
        }
        String signature = "<br><br>Thanks!<br>Zero-in team";
        String content = "Good day! <br><br>" + event.getContent() + href + event.getContent2() + signature;

        eMail.setSubject(subject);
        eMail.setContent(content);
        mailService.sendEmail(eMail);
    }
}
