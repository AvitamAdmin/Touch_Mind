package com.touchMind.core.event;

import com.touchMind.core.mongo.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private User user;

    private String subject;

    private String content;
    private String content2;

    private String toAddress;

    private String level;

    public OnRegistrationCompleteEvent(
            User user, String appUrl, String subject, String content, String toAddress, String level, String content2) {
        super(user);

        this.user = user;
        this.appUrl = appUrl;
        this.subject = subject;
        this.content = content;
        this.content2 = content2;
        this.toAddress = toAddress;
        this.level = level;
    }
}
