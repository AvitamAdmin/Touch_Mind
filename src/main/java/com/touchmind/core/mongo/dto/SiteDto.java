package com.touchmind.core.mongo.dto;

import com.touchmind.core.mongo.dto.CommonDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SiteDto extends CommonDto {
    private String affiliateId;
    private String affiliateName;
    private String siteChannel;
    private String secretKey;
   // private String subsidiary;
}
