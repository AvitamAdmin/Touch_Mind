package com.touchMind.qa.actions;

import com.aventstack.extentreports.Status;
import com.touchMind.core.SpringContext;
import com.touchMind.core.mongo.dto.ReportDto;
import com.touchMind.core.mongo.model.ConditionGroup;
import com.touchMind.core.mongo.model.Node;
import com.touchMind.core.mongo.model.QaLocatorResultReport;
import com.touchMind.core.mongo.model.baseEntity.BaseEntity;
import com.touchMind.core.mongo.repository.NodeRepository;
import com.touchMind.core.mongo.repository.QaLocatorResultReportRepository;
import com.touchMind.form.LocatorGroupData;
import com.touchMind.qa.framework.ThreadTestContext;
import com.touchMind.qa.service.ActionRequest;
import com.touchMind.qa.service.ActionResult;
import com.touchMind.qa.service.ElementActionService;
import com.touchMind.qa.service.QualityAssuranceService;
import com.touchMind.qa.service.SelectorService;
import com.touchMind.qa.strategies.ActionType;
import com.touchMind.qa.utils.ReportUtils;
import com.touchMind.qa.utils.TestDataUtils;
import com.touchMind.qa.validation.AndConditionHandler;
import com.touchMind.qa.validation.OrConditionHandler;
import com.touchMind.qa.validation.ToolkitValidationHandler;
import com.touchMind.tookit.service.ReportService;
import com.touchMind.utils.CommonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service(ActionType.TOOL_KIT_API_VALIDATION_ACTION)
public class ToolkitApiSkuValidationAction implements ElementActionService {

    public static final String EQUALS = "Equals";
    public static final String NOT_EMPTY = "Not empty";
    @Autowired
    private QualityAssuranceService qualityAssuranceService;
    @Autowired
    private SelectorService selectorService;
    @Autowired
    private QaLocatorResultReportRepository qaLocatorResultReportRepository;

    @Override
    public ActionResult performAction(ActionRequest actionRequest) {
        ITestContext context = actionRequest.getContext();
        LocatorGroupData locatorGroupData = actionRequest.getLocatorGroupData();

        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        List<ConditionGroup> conditionGroupList = locatorGroupData.getConditionGroupList();
        boolean isDebug = BooleanUtils.toBoolean(TestDataUtils.getString(testData, TestDataUtils.Field.IS_DEBUG));
        ActionResult actionResult = new ActionResult();
        actionResult.setStepStatus(Status.FAIL);
        if (CollectionUtils.isNotEmpty(conditionGroupList)) {
            try {
                AtomicBoolean isFirstHandler = new AtomicBoolean(true);
                ToolkitValidationHandler previousHandler = null;
                ToolkitValidationHandler nextHandler = null;
                ToolkitValidationHandler startHandler = null;
                ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
                String sku = TestDataUtils.getString(testData, TestDataUtils.Field.SKU, threadTestContext.getTestIdentifier());
                for (ConditionGroup conditionGroup : conditionGroupList) {
                    ReportService reportService = SpringContext.getBean(ReportService.class);
                    ReportDto reportDto = getReportInputDataForProcessing(context, conditionGroup.getToolkitId(), ObjectUtils.isNotEmpty(sku) ? sku : "");
                    List<BaseEntity> allRecords = reportService.getReport(reportDto);
                    String paramName = conditionGroup.getParamName();
                    Object apiObject = null;
                    if (CollectionUtils.isNotEmpty(allRecords)) {
                        BaseEntity baseEntity = allRecords.get(0);
                        if (baseEntity != null) {
                            apiObject = baseEntity.getRecords().get(paramName);
                        }
                    }
                    if (apiObject == null) {
                        String errorMsg = "Validation failed " + conditionGroup.getParamValue() + " " + conditionGroup.getCondition() + " " + apiObject;
                        ReportUtils.fail(context, errorMsg, StringUtils.EMPTY, false);
                        qualityAssuranceService.saveErrorData(null, testData, null, errorMsg);
                        actionResult.setStepStatus(Status.FAIL);
                        return actionResult;
                    }

                    if (isFirstHandler.get()) {
                        startHandler = conditionGroup.getIsOrChain() != null && conditionGroup.getIsOrChain().booleanValue() ? new AndConditionHandler() : new OrConditionHandler();
                        previousHandler = startHandler;
                        setConditionData(context, startHandler, reportDto.getCurrentVariant(), conditionGroup, apiObject.toString());
                    } else {
                        nextHandler = conditionGroup.getIsOrChain() != null && conditionGroup.getIsOrChain().booleanValue() ? new AndConditionHandler() : new OrConditionHandler();
                        setConditionData(context, nextHandler, reportDto.getCurrentVariant(), conditionGroup, apiObject.toString());
                        previousHandler.setNextValidationHandler(nextHandler);
                        previousHandler = nextHandler;
                    }
                    isFirstHandler.set(false);
                }
                if (startHandler != null) {
                    actionResult.setStepStatus(startHandler.validate(true) ? Status.INFO : Status.FAIL);
                    return actionResult;
                }
            } catch (Exception exp) {
                String errorMsg = "Toolkit Validation failed " + exp.getMessage();
                ReportUtils.logMessage(context, isDebug, errorMsg + exp.getMessage());
                ReportUtils.fail(context, errorMsg, StringUtils.EMPTY, false);
                ReportUtils.logMessage(context, isDebug, errorMsg + " Save to result " + errorMsg + " test data: " + testData);
                qualityAssuranceService.saveErrorData(null, testData, null, errorMsg);
                actionResult.setStepStatus(Status.FAIL);
                return actionResult;
            }
        }
        QaLocatorResultReport qaLocatorResultReport = new QaLocatorResultReport();
        qaLocatorResultReport = qaLocatorResultReport.getQaLocatorResultReport(actionRequest.getQaTestResultId(), StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, Status.INFO, null, ActionType.TOOL_KIT_API_VALIDATION_ACTION);
        qaLocatorResultReportRepository.save(qaLocatorResultReport);
        actionResult.setStepStatus(Status.PASS);
        return actionResult;
    }

    private ReportDto getReportInputDataForProcessing(ITestContext context, String nodeId, String sku) {
        NodeRepository nodeRepository = SpringContext.getBean(NodeRepository.class);
        //TODO check if the record is correctly fetched
        Node currentNode = nodeRepository.findByIdentifier(nodeId);
        JSONObject testDataJson = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        ReportDto reportDto = new ReportDto();
        if (currentNode != null) {
            if (testDataJson.has(TestDataUtils.Field.TOOLKIT_SESSIONS.toString() + currentNode.getId())) {
                reportDto.setCurrentSessionId(testDataJson.getString(TestDataUtils.Field.TOOLKIT_SESSIONS.toString() + currentNode.getId()));
            }
            //TODO check if the record is correctly fetched
            reportDto.setCurrentNode(currentNode.getIdentifier());
            reportDto.setMapping(CommonUtil.getCollectionName(currentNode.getPath()));
            reportDto.setSkus(sku);
            String isoSiteCode = TestDataUtils.getString(testDataJson, TestDataUtils.Field.SITE_ISOCODE);
            reportDto.setSites(List.of(isoSiteCode));
         //   reportDto.setSubsidiary(getSubsidiaryForId(testDataJson));
        }
        return reportDto;
    }


//    private Subsidiary getSubsidiaryForId(JSONObject testDataJson) {
//        String subsidiaryId = (String) testDataJson.get(TestDataUtils.Field.SUBSIDIARY.toString());
//        if (StringUtils.isEmpty(subsidiaryId)) {
//            return null;
//        }
//        SubsidiaryRepository subsidiaryRepository = SpringContext.getBean(SubsidiaryRepository.class);
//        //TODO check if the record is correctly fetched
//        return subsidiaryRepository.findByIdentifier(subsidiaryId);
//    }

    private void setConditionData(ITestContext context, ToolkitValidationHandler startHandler, String currentVariant, ConditionGroup conditionGroup, String apiValue) {
        startHandler.setCurrentVariant(currentVariant);
        startHandler.setParamValue(conditionGroup.getParamValue());
        startHandler.setCondition(conditionGroup.getCondition());
        startHandler.setApiValue(apiValue);
        startHandler.setContext(context);
    }
}
