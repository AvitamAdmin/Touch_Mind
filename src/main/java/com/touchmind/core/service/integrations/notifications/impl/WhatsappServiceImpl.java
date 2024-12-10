package com.touchmind.core.service.integrations.notifications.impl;

import com.touchmind.core.mongotemplate.repository.QARepository;
import com.touchmind.core.service.integrations.notifications.WhatsappService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;


@Service
public class WhatsappServiceImpl implements WhatsappService {

    private final String phoneNumberFrom = "+14155238886";
    Logger LOG = LoggerFactory.getLogger(WhatsappServiceImpl.class);
    @Autowired
    private QARepository qaRepository;
    @Value("${TWILIO_ACCOUNT_SID}")
    private String ACCOUNT_SID;
    @Value("${TWILIO_AUTH_TOKEN}")
    private String AUTH_TOKEN;

    @Override
    public void processAlerts(String recipient, String alertMessage) {
        if (StringUtils.isEmpty(recipient)) {
            LOG.error("Missing recipient number ===>  " + recipient + " No alert can be processed !");
            return;
        }

        //labelAndMessage.put("locatorTestCaseName",testLocatorGroup.getShortDescription());
        //labelAndMessage.put("locatorTestCaseIdentifier",testLocatorGroup.getIdentifier());
        //labelAndMessage.put("passedTests",String.valueOf(qaTestResult.getTestPassedCount()));

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        String[] recipients = recipient.split(",");
        Arrays.stream(recipients).forEach(toNumber -> {
            Message message = Message.creator(
                            new com.twilio.type.PhoneNumber("whatsapp:" + toNumber),
                            new com.twilio.type.PhoneNumber("whatsapp:" + phoneNumberFrom),
                            alertMessage)
                    .create();
            LOG.info("Alert message was sent tp ===>  " + recipient + " : " + message.getSid());
        });
    }
}





