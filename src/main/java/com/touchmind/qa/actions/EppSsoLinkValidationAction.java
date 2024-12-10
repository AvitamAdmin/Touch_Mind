package com.touchmind.qa.actions;

import com.touchmind.core.mongo.dto.EppSsoWsDto;
import com.touchmind.core.mongo.model.EppSso;
import com.touchmind.core.mongo.model.Site;
import com.touchmind.core.mongo.model.Subsidiary;
import com.touchmind.core.mongo.repository.SiteRepository;
import com.touchmind.core.mongo.repository.SubsidiaryRepository;
import com.touchmind.core.service.EppSsoService;
import com.touchmind.core.service.impl.EppSsoServiceImpl;
import com.touchmind.form.LocatorGroupData;
import com.touchmind.qa.framework.ThreadTestContext;
import com.touchmind.qa.service.ActionRequest;
import com.touchmind.qa.service.ActionResult;
import com.touchmind.qa.service.ElementActionService;
import com.touchmind.qa.service.QualityAssuranceService;
import com.touchmind.qa.strategies.ActionType;
import com.touchmind.qa.utils.ReportUtils;
import com.touchmind.qa.utils.TestDataUtils;
import static com.touchmind.qa.utils.WaitUtils.COLON_SPACE_QUOTES;
import static com.touchmind.qa.utils.WaitUtils.QUOTES_DOT_SPACE;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

@Service(ActionType.VALIDATE_EPP_SSO_LINK)
public class EppSsoLinkValidationAction implements ElementActionService {

    @Autowired
    private EppSsoService eppSsoService;

    @Autowired
    private SubsidiaryRepository subsidiaryRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private QualityAssuranceService qualityAssuranceService;

    @Override
    public ActionResult performAction(ActionRequest actionRequest) {
        ITestContext context = actionRequest.getContext();
        LocatorGroupData locatorGroupData = actionRequest.getLocatorGroupData();

        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        boolean isDebug = BooleanUtils.toBoolean(TestDataUtils.getString(testData, TestDataUtils.Field.IS_DEBUG));
        ActionResult actionResult = new ActionResult();
        if (locatorGroupData.isCheckEppSso()) {
            String itemSite = TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE);
            String subsidiaryId = testData.getString(TestDataUtils.Field.SUBSIDIARY.toString());
            Subsidiary subsidiary = subsidiaryRepository.findByRecordId(subsidiaryId);
            ReportUtils.info(context,
                    "Validation for " +
                            COLON_SPACE_QUOTES +
                            QUOTES_DOT_SPACE, StringUtils.EMPTY,
                    locatorGroupData.isTakeAScreenshot());
            if (subsidiary == null) {
                String errorMsg = "Eppsso validation failed invalid Subsidiary " +
                        subsidiaryId;
                ReportUtils.fail(context, errorMsg
                        , StringUtils.EMPTY,
                        locatorGroupData.isTakeAScreenshot());
                qualityAssuranceService.saveErrorData(null, testData, null, errorMsg);
                //actionResult.setActionResult(ActionType.VALIDATE_EPP_SSO_LINK, errorMsg, null, Status.FAIL);
                return actionResult;
            }
            EppSso eppSso = null;
            String redirectURL = null;
            try {
                Site site = siteRepository.findByRecordId(itemSite);
                if (site == null) {
                    String errorMsg = "Eppsso validation failed invalid site " +
                            " Eppsso link " + eppSso.getSsoLink();
                    ReportUtils.fail(context, errorMsg
                            , StringUtils.EMPTY,
                            locatorGroupData.isTakeAScreenshot());
                    qualityAssuranceService.saveErrorData(null, testData, null, errorMsg);
                    //actionResult.setActionResult(ActionType.VALIDATE_EPP_SSO_LINK, errorMsg, null, Status.FAIL);
                    return actionResult;
                }
                String siteId = site.getIdentifier();
                ReportUtils.info(context,
                        locatorGroupData.getGroupId() +
                                COLON_SPACE_QUOTES +
                                " Validation started for " + siteId +
                                QUOTES_DOT_SPACE + " and for subsidiary: " + subsidiary.getIdentifier(), StringUtils.EMPTY,
                        locatorGroupData.isTakeAScreenshot());
                EppSsoWsDto eppSsoWsDto = new EppSsoWsDto();
                eppSsoWsDto.setSubsidiary(subsidiary.getRecordId());
                eppSso = eppSsoService.generateSsoLink(String.valueOf(site.getId()), eppSsoWsDto);
                threadTestContext.getDriver().get(eppSso.getSsoLink());
                redirectURL = threadTestContext.getDriver().getCurrentUrl();
                boolean result = StringUtils.isNotEmpty(redirectURL) && redirectURL.contains(EppSsoServiceImpl.MULTISTORE + siteId);
                if (result) {
                    ReportUtils.info(context,
                            locatorGroupData.getGroupId() +
                                    COLON_SPACE_QUOTES +
                                    " Eppsso link: " + eppSso.getSsoLink() +
                                    QUOTES_DOT_SPACE + " Result : " + redirectURL, StringUtils.EMPTY,
                            locatorGroupData.isTakeAScreenshot());
                } else {
                    ReportUtils.fail(context,
                            locatorGroupData.getGroupId() +
                                    COLON_SPACE_QUOTES +
                                    " Eppsso link: " + eppSso.getSsoLink() +
                                    QUOTES_DOT_SPACE + " Result : " + redirectURL, StringUtils.EMPTY,
                            locatorGroupData.isTakeAScreenshot());
                }
                //actionResult.setActionResult(ActionType.VALIDATE_EPP_SSO_LINK, ActionType.VALIDATE_EPP_SSO_LINK, null, result ? Status.FAIL : Status.INFO);
                return actionResult;
            } catch (Exception e) {
                String errorMsg = locatorGroupData.getGroupId() +
                        " Eppsso link " + eppSso.getSsoLink() + " " + redirectURL;
                ReportUtils.fail(context, errorMsg
                        , StringUtils.EMPTY,
                        locatorGroupData.isTakeAScreenshot());
                ReportUtils.logMessage(context, isDebug, errorMsg + e.getMessage());
                qualityAssuranceService.saveErrorData(null, testData, null, errorMsg);
                //actionResult.setActionResult(ActionType.VALIDATE_EPP_SSO_LINK, ActionType.VALIDATE_EPP_SSO_LINK, null, Status.FAIL);
                return actionResult;
            }

        }
        //actionResult.setActionResult(ActionType.VALIDATE_EPP_SSO_LINK, ActionType.VALIDATE_EPP_SSO_LINK, null, Status.INFO);
        return actionResult;
    }
}
