package com.touchMind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CommonWsDto extends PaginationDto {
    private String redirectUrl;
    private String message;
    private String baseUrl;
    private String fileName;
    private MultipartFile file;
    private boolean success = true;
    private String operator;
    private List<SearchDto> attributeList;
    private String node;
    private List<SavedQueryDto> savedQuery;
    private Map<String, String> headerFields;
}
