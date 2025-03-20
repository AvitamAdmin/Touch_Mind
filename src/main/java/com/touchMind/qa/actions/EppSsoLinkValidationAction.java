//package com.touchMind.qa.actions;
//
//import com.aventstack.extentreports.Status;
//import com.touchMind.core.mongo.dto.EppSsoWsDto;
//import com.touchMind.core.mongo.model.EppSso;
//import com.touchMind.core.mongo.model.QaLocatorResultReport;
//import com.touchMind.core.mongo.model.Site;
//import com.touchMind.core.mongo.repository.QaLocatorResultReportRepository;
//import com.touchMind.core.mongo.repository.SiteRepository;
//import com.touchMind.core.service.EppSsoService;
//import com.touchMind.core.service.impl.EppSsoServiceImpl;
//import com.touchMind.form.LocatorGroupData;
//import com.touchMind.qa.framework.ThreadTestContext;
//import com.touchMind.qa.service.ActionRequest;
//import com.touchMind.qa.service.ActionResult;
//import com.touchMind.qa.service.ElementActionService;
//import com.touchMind.qa.service.QualityAssuranceService;
//import com.touchMind.qa.service.SelectorService;
//import com.touchMind.qa.strategies.ActionType;
//import com.touchMind.qa.utils.ReportUtils;
//import com.touchMind.qa.utils.TestDataUtils;
//import org.apache.commons.lang3.BooleanUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.testng.ITestContext;
//
//import static com.touchMind.qa.utils.WaitUtils.COLON_SPACE_QUOTES;
//import static com.touchMind.qa.utils.WaitUtils.QUOTES_DOT_SPACE;
//
//@Service(ActionType.VALIDATE_EPP_SSO_LINK)
//public class EppSsoLinkValidationAction implements ElementActionService {
//
//    @Autowired
//    private EppSsoService eppSsoService;
//
////    @Autowired
////    private SubsidiaryRepository subsidiaryRepository;
//
//    @Autowired
//    private SiteRepository siteRepository;
//
//    @Autowired
//    private QualityAssuranceService qualityAssuranceService;
//
//    @Autowired
//    private SelectorService selectorService;
//    @Autowired
//    private QaLocatorResultReportRepository qaLocatorResultReportRepository;
//
//    @Override
//    public ActionResult performAction(ActionRequest actionRequest) {
//        ITestContext context = actionRequest.getContext();
//        LocatorGroupData locatorGroupData = actionRequest.getLocatorGroupData();
//
//        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
//        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
//        boolean isDebug = BooleanUtils.toBoolean(TestDataUtils.getString(testData, TestDataUtils.Field.IS_DEBUG));
//        ActionResult actionResult = new ActionResult();
//        actionResult.setStepStatus(Status.FAIL);
//        if (locatorGroupData.isCheckEppSso()) {
//            String itemSite = TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE);
//            String subsidiaryId = testData.getString(TestDataUtils.Field.SUBSIDIARY.toString());
//          //  Subsidiary subsidiary = subsidiaryRepository.findByIdentifier(subsidiaryId);
//            ReportUtils.info(context,
//                    "Validation for " +
//                            COLON_SPACE_QUOTES +
//                            QUOTES_DOT_SPACE, StringUtils.EMPTY,
//                    locatorGroupData.isTakeAScreenshot());
//            //if (subsidiary == null) {
//                String errorMsg = "Eppsso validation failed invalid Subsidiary " +
//                        subsidiaryId;
//                ReportUtils.fail(context, errorMsg
//                        , StringUtils.EMPTY,
//                        locatorGroupData.isTakeAScreenshot());
//                qualityAssuranceService.saveErrorData(null, testData, null, errorMsg);
//                actionResult.setStepStatus(Status.FAIL);
//                return actionResult;
//            }
//            EppSso eppSso = null;
//            String redirectURL = null;
//            try {
//               // Site site = siteRepository.findByIdentifier(itemSite);
//                if (site == null) {
//                    String errorMsg = "Eppsso validation failed invalid site ";
//                    ReportUtils.fail(context, errorMsg
//                            , StringUtils.EMPTY,
//                            locatorGroupData.isTakeAScreenshot());
//                    qualityAssuranceService.saveErrorData(null, testData, null, errorMsg);
//                    actionResult.setStepStatus(Status.FAIL);
//                    return actionResult;
//                }
//                String siteId = site.getIdentifier();
////                ReportUtils.info(context,
////                        locatorGroupData.getGroupId() +
////                                COLON_SPACE_QUOTES +
////                                " Validation started for " + siteId +
////                                //QUOTES_DOT_SPACE + " and for subsidiary: " + subsidiary.getIdentifier(), StringUtils.EMPTY,
////                        locatorGroupData.isTakeAScreenshot());
//                EppSsoWsDto eppSsoWsDto = new EppSsoWsDto();
//                //eppSsoWsDto.setSubsidiary(subsidiary.getIdentifier());
//                eppSso = eppSsoService.generateSsoLink(String.valueOf(site.getId()), eppSsoWsDto);
//                if (eppSso != null) {
//                    threadTestContext.getDriver().get(eppSso.getSsoLink());
//                    redirectURL = threadTestContext.getDriver().getCurrentUrl();
//                    boolean result = StringUtils.isNotEmpty(redirectURL) && redirectURL.contains(EppSsoServiceImpl.MULTISTORE + siteId);
//                    if (result) {
//                        ReportUtils.info(context,
//                                locatorGroupData.getGroupId() +
//                                        COLON_SPACE_QUOTES +
//                                        " Eppsso link: " + eppSso.getSsoLink() +
//                                        QUOTES_DOT_SPACE + " Result : " + redirectURL, StringUtils.EMPTY,
//                                locatorGroupData.isTakeAScreenshot());
//                    } else {
//                        ReportUtils.fail(context,
//                                locatorGroupData.getGroupId() +
//                                        COLON_SPACE_QUOTES +
//                                        " Eppsso link: " + eppSso.getSsoLink() +
//                                        QUOTES_DOT_SPACE + " Result : " + redirectURL, StringUtils.EMPTY,
//                                locatorGroupData.isTakeAScreenshot());
//                    }
//                    actionResult.setStepStatus(result ? Status.FAIL : Status.PASS);
//                    return actionResult;
//                }
//
//            } catch (Exception e) {
//                String errorMsg = "Exception in Eppsso Validation";
//                ReportUtils.fail(context, errorMsg
//                        , StringUtils.EMPTY,
//                        locatorGroupData.isTakeAScreenshot());
//                ReportUtils.logMessage(context, isDebug, errorMsg + e.getMessage());
//                qualityAssuranceService.saveErrorData(null, testData, null, errorMsg);
//                actionResult.setStepStatus(Status.FAIL);
//                return actionResult;
//            }
//
//        }
////        QaLocatorResultReport qaLocatorResultReport = new QaLocatorResultReport();
////        qaLocatorResultReport = qaLocatorResultReport.getQaLocatorResultReport(actionRequest.getQaTestResultId(), StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, Status.INFO, null, ActionType.VALIDATE_EPP_SSO_LINK);
////        qaLocatorResultReportRepository.save(qaLocatorResultReport);
////        actionResult.setStepStatus(Status.PASS);
////        return actionResult;
//    }
//}
