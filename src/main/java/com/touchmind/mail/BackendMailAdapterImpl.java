package com.touchmind.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class BackendMailAdapterImpl implements BackendMailAdapter {

    @Autowired
    private CheilMailService cheilMailService;

    @Override
    public String getBackendResponse(Date curentDate, boolean onlyNewMessages) throws EmailNotRecievedException {
        return cheilMailService.getRegistrationDeepLink(curentDate, onlyNewMessages);
    }

    @Override
    public String getBackendResponseFallback(RuntimeException e) {
        return "Give up no mail received please check if email is correct";
    }
}
