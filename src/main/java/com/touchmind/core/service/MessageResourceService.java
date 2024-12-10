package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.MessageResourceWsDto;
import com.touchmind.core.mongo.model.MessageResource;
import com.touchmind.form.MessageResourceForm;
import org.json.JSONObject;

import java.util.List;


public interface MessageResourceService {
    List<MessageResource> findAll();

    void deleteByRecordId(String id);

    MessageResourceForm addMessage(MessageResourceForm messageResourceForm);

    MessageResourceForm editMessage(String id);

    void processNotifications(JSONObject testMapData);

    MessageResourceWsDto handleEdit(MessageResourceWsDto request);
}
