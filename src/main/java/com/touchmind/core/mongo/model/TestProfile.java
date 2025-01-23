package com.touchmind.core.mongo.model;

import com.touchmind.core.mongo.dto.ProfileLocatorDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("TestProfile")
@Getter
@Setter
@NoArgsConstructor
public class TestProfile extends CommonFields {
    List<ProfileLocatorDto> profileLocators;
    private String subsidiary;
    private boolean encrypted;
}
