package com.touchmind.core.service.impl;

import com.touchmind.core.mongo.dto.MessageResourceDto;
import com.touchmind.core.mongo.dto.MessageResourceWsDto;
import com.touchmind.core.mongo.model.MessageResource;
import com.touchmind.core.mongo.model.TestLocatorGroup;
import com.touchmind.core.mongo.model.TestPlan;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.MessageResourceRepository;
import com.touchmind.core.mongo.repository.QaTestPlanRepository;
import com.touchmind.core.mongo.repository.TestLocatorGroupRepository;
import com.touchmind.core.mongotemplate.QATestResult;
import com.touchmind.core.mongotemplate.repository.QARepository;
import com.touchmind.core.service.BaseService;
import com.touchmind.core.service.CoreService;
import com.touchmind.core.service.MessageResourceService;
import com.touchmind.core.service.integrations.notifications.WhatsappService;
import com.touchmind.form.MessageResourceForm;
import com.touchmind.qa.utils.TestDataUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class MessageResourceServiceImpl implements MessageResourceService {

    public static final String ADMIN_MESSAGES = "/admin/message";
    @Autowired
    private MessageResourceRepository messageResourceRepository;
    @Autowired
    private TestLocatorGroupRepository testLocatorGroupRepository;
    @Autowired
    private QARepository qaRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private WhatsappService whatsappService;
    @Autowired
    private QaTestPlanRepository qaTestPlanRepository;
    @Autowired
    private CoreService coreService;
    @Autowired
    private BaseService baseService;

    @Override
    public List<MessageResource> findAll() {
        return messageResourceRepository.findAll();
    }

    @Override
    public void deleteByRecordId(String id) {
        messageResourceRepository.deleteByRecordId(id);
    }

    @Override
    public MessageResourceForm addMessage(MessageResourceForm messageResourceForm) {
        MessageResource messageResource = modelMapper.map(messageResourceForm, MessageResource.class);
        if (messageResource.getRecordId() != null) {
            MessageResource messageResourceOptional = messageResourceRepository.findByRecordId(messageResource.getRecordId());
            if (messageResourceOptional != null) {
                MessageResource oldMessage = messageResourceOptional;
                oldMessage.setType(messageResource.getType());
                oldMessage.setDescription(messageResource.getDescription());
                oldMessage.setTestPlanId(messageResource.getTestPlanId());
                oldMessage.setPercentFailure(messageResource.getPercentFailure());
                oldMessage.setStatus(messageResource.getStatus());
                oldMessage.setRecipients(messageResource.getRecipients());
                messageResourceRepository.save(oldMessage);
            }
        } else {
            messageResourceRepository.save(messageResource);
        }
        if (messageResource.getRecordId() == null) {
            messageResource.setRecordId(String.valueOf(messageResource.getId().getTimestamp()));
            messageResourceRepository.save(messageResource);
        }
        return messageResourceForm;
    }

    @Override
    public MessageResourceForm editMessage(String id) {
        MessageResource messageResourceOptional = messageResourceRepository.findByRecordId(id);
        if (messageResourceOptional != null) {
            return modelMapper.map(messageResourceOptional, MessageResourceForm.class);
        }
        return null;
    }

    private Map<String, Integer> isSubscribedForNotification(JSONObject testMapData) {
        String processType = TestDataUtils.getString(testMapData, TestDataUtils.Field.JOB_TYPE);
        //Check if it is cronjob if not no need to store value
        if (StringUtils.isEmpty(processType) || !"cronJob".equals(processType)) return null;

        //CHeck if any groups are assigned to notifications
        String objectId = testMapData.getString(TestDataUtils.Field.TEST_PLAN.toString());
        Map<String, Integer> notificationLocatorGroups = getLocatorGroupsForTestPlan(objectId);
        if (notificationLocatorGroups != null && notificationLocatorGroups.isEmpty()) return null;
        return notificationLocatorGroups;
    }

    @Override
    public void processNotifications(JSONObject testData) {
        Map<String, Integer> locatorGroups = isSubscribedForNotification(testData);
        if (locatorGroups != null && locatorGroups.isEmpty()) return;
        String objectId = testData.getString(TestDataUtils.Field.TEST_PLAN.toString());
        String sessionId = TestDataUtils.getString(testData, TestDataUtils.Field.SESSION_ID);
        locatorGroups.keySet().forEach(locatorGroup -> {
            TestLocatorGroup testLocatorGroup = testLocatorGroupRepository.findByRecordId(locatorGroup);
            if (testLocatorGroup == null) return;
            List<QATestResult> allTests = qaRepository.findBySessionIdAndLocatorGroupIdentifier(sessionId, testLocatorGroup.getIdentifier());
            int totalSkus = allTests.size();
            Map<String, String> labelAndMessage = new HashMap<>();
            Map<String, Integer> labelAndCount = new HashMap<>();

            AtomicReference<String> passedTestCount = new AtomicReference<>("");
            allTests.forEach(qaTestResult -> {
                passedTestCount.set(String.valueOf(qaTestResult.getTestPassedCount()));
                processRecord(labelAndMessage, labelAndCount, qaTestResult);
            });
            //Check for each label if notification is required
            Map<String, Integer> alertLabels = new HashMap<>();
            labelAndMessage.keySet().stream().forEach(label -> {
                setNotifications(alertLabels, label, locatorGroups.get(locatorGroup), totalSkus, labelAndCount.get(label));
            });
            //Finally, prepare message to send
            StringBuffer buffer = new StringBuffer();
            MessageResource messageResource = getMessageResourceForTestPlan(objectId);
            if (messageResource == null) return;
            alertLabels.keySet().forEach(label -> {
                buffer.append(testLocatorGroup.getIdentifier() + " > " + testLocatorGroup.getShortDescription() + "\n Passed SKU count: " + passedTestCount.get() + "\n Cause " + label + " > " + alertLabels.get(label) + "% failed! " + labelAndMessage.get(label) + "!\n");
            });
            whatsappService.processAlerts(messageResource.getRecipients(), buffer.toString());
        });
    }

    @Override
    public MessageResourceWsDto handleEdit(MessageResourceWsDto request) {
        MessageResourceWsDto messageResourceWsDto = new MessageResourceWsDto();
        MessageResource requestData = null;
        List<MessageResourceDto> messageResources = request.getMessageResources();
        List<MessageResource> messageResourceList = new ArrayList<>();
        for (MessageResourceDto messageResource : messageResources) {
            if (messageResource.getRecordId() != null) {
                requestData = messageResourceRepository.findByRecordId(messageResource.getRecordId());
                modelMapper.map(messageResource, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.MESSAGES, messageResource.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(messageResource, MessageResource.class);
            }
            baseService.populateCommonData(requestData);
            messageResourceRepository.save(requestData);
            if (messageResource.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            messageResourceWsDto.setBaseUrl(ADMIN_MESSAGES);
            messageResourceRepository.save(requestData);
            messageResourceList.add(requestData);
        }
        messageResourceWsDto.setMessageResources(modelMapper.map(messageResourceList, List.class));
        return messageResourceWsDto;
    }

    private void processRecord(Map<String, String> labelAndMessage, Map<String, Integer> labelAndCount, QATestResult qaTestResult) {
        Map<String, Set<String>> errorMap = qaTestResult.getErrorMap();
        errorMap.keySet().stream().forEach(label -> {
            Set<String> errorMessages = errorMap.get(label);
            labelAndMessage.put(label, errorMessages != null ? errorMessages.toString() : "");
            if (labelAndMessage.containsKey(label)) {
                Integer oldErrorCound = labelAndCount.get(label);
                int errorCount = oldErrorCound != null ? oldErrorCound : 0;
                labelAndCount.put(label, errorCount + 1);
            } else {
                labelAndCount.put(label, 1);
            }
        });
    }

    private boolean setNotifications(Map<String, Integer> alertLabels, String label, int alertLevel, int totalExecutions, int errorLevel) {
        boolean result = alertLevel < (errorLevel / totalExecutions * 100);
        if (result) {
            alertLabels.put(label, errorLevel / totalExecutions * 100);
        }
        return result;
    }

    public Map<String, Integer> getLocatorGroupsForTestPlan(String testPlanId) {
        MessageResource messageResource = getMessageResourceForTestPlan(testPlanId);
        if (messageResource == null) return new HashMap<>();
        Map<String, Integer> locatorGroups = new HashMap<>();
        TestPlan testPlan = qaTestPlanRepository.findByRecordId(testPlanId);
        if (testPlan != null) {
            testPlan.getTestLocatorGroups().forEach(testLocatorGroupId -> {
                locatorGroups.put(testLocatorGroupId, messageResource.getPercentFailure());
            });
        }
        return locatorGroups;
    }

    private MessageResource getMessageResourceForTestPlan(String testPlanId) {
        List<MessageResource> messageResourceList = messageResourceRepository.findByTestPlanId(testPlanId);
        Optional<MessageResource> messageResourceOptional = messageResourceList.stream().findFirst();
        return messageResourceOptional.isPresent() ? messageResourceOptional.get() : null;
    }
}
