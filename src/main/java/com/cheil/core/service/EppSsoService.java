package com.cheil.core.service;

import com.cheil.core.mongo.dto.EppSsoWsDto;
import com.cheil.core.mongo.model.EppSso;

import java.text.ParseException;

public interface EppSsoService {
    EppSso generateSsoLink(String site, EppSsoWsDto eppSsoWsDto) throws ParseException;

    String getString(String ssoDate, String timeZone) throws ParseException;
}
