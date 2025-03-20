package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.MessageResourceWsDto;
import com.touchMind.core.mongo.model.MessageResource;
import com.touchMind.form.MessageResourceForm;
import org.json.JSONObject;

import java.util.List;


public interface MessageResourceService {
    List<MessageResource> findAll();

    void deleteByIdentifier(String id);

    MessageResourceForm addMessage(MessageResourceForm messageResourceForm);

    MessageResourceForm editMessage(String id);

    void processNotifications(JSONObject testMapData);

    MessageResourceWsDto handleEdit(MessageResourceWsDto request);
}
