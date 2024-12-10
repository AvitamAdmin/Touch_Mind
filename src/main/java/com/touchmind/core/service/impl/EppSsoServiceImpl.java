package com.touchmind.core.service.impl;

import com.touchmind.core.mongo.dto.EppSsoWsDto;
import com.touchmind.core.mongo.model.Environment;
import com.touchmind.core.mongo.model.EnvironmentConfig;
import com.touchmind.core.mongo.model.EppSso;
import com.touchmind.core.mongo.model.Site;
import com.touchmind.core.mongo.model.Subsidiary;
import com.touchmind.core.mongo.repository.EnvironmentRepository;
import com.touchmind.core.mongo.repository.SiteRepository;
import com.touchmind.core.mongo.repository.SubsidiaryRepository;
import com.touchmind.core.service.EppSsoService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class EppSsoServiceImpl implements EppSsoService {

    public static final String MULTISTORE = "/multistore/";
    public static final String CLICKTIMESTAMP = "&clicktimestamp=";
    public static final String AFFILIATEID = "&affiliateid=";
    public static final String LOGIN_REWARDSPARTNER_AFFILIATENAME = "/login/rewardspartner?affiliatename=";
    public static final String HASH = "&hash=";
    public static final String SLASH = "/";
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    SimpleDateFormat ssoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Autowired
    private SubsidiaryRepository subsidiaryRepository;

    @Value("${epp.shop.sso.link}")
    private String getEppShopSsoLink;

    @Override
    public EppSso generateSsoLink(String site, EppSsoWsDto eppSsoWsDto) throws ParseException {
        Site siteModel = siteRepository.findByRecordId(site);
        String date = getString(eppSsoWsDto.getSsoDate(), eppSsoWsDto.getTimeZone());
        String message = siteModel.getAffiliateId() + date + siteModel.getSecretKey();
        String hash = DigestUtils.md5Hex(message);
        if (StringUtils.isNotEmpty(eppSsoWsDto.getEnvironment())) {
            Environment environment = environmentRepository.findByRecordId(eppSsoWsDto.getEnvironment());
            if (environment != null) {
                List<EnvironmentConfig> configs = environment.getConfigs();
                if (CollectionUtils.isNotEmpty(configs)) {
                    EnvironmentConfig config = configs.get(0);
                    String url = config.getUrl();
                    getEppShopSsoLink = StringUtils.isNotEmpty(url) ? url : getEppShopSsoLink;
                    if (!getEppShopSsoLink.endsWith("/")) {
                        getEppShopSsoLink = getEppShopSsoLink + "/";
                    }
                }
            }
        }
        String subsidiary = eppSsoWsDto.getSubsidiary();
        Subsidiary subsidiary1 = subsidiaryRepository.findByRecordId(subsidiary);
        String md5Link = getEppShopSsoLink + subsidiary1.getIsoCode() + MULTISTORE + siteModel.getSiteChannel() + SLASH + siteModel.getIdentifier() + LOGIN_REWARDSPARTNER_AFFILIATENAME +
                siteModel.getAffiliateName() + AFFILIATEID + siteModel.getAffiliateId() + CLICKTIMESTAMP
                + date + HASH + hash;
        EppSso eppSso = new EppSso();
        eppSso.setSsoLink(md5Link);
        eppSso.setDisabledLink(md5Link);
        eppSso.setAffiliateId(siteModel.getAffiliateId());
        eppSso.setTimestamp(date);
        eppSso.setHash(hash);
        return eppSso;
    }

    @Override
    public String getString(String ssoDate, String timeZone) throws ParseException {
        ZonedDateTime zoneDate = StringUtils.isNotEmpty(timeZone) ? ZonedDateTime.now(ZoneId.of(timeZone)) : ZonedDateTime.now();
        return StringUtils.isNotEmpty(ssoDate) ? dateFormat.format(ssoFormat.parse(ssoDate)) : dateFormat.format(inputFormat.parse(zoneDate.toString().split("\\.")[0]));
    }
}
