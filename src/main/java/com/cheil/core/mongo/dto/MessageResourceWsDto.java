package com.cheil.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MessageResourceWsDto extends CommonWsDto {
    private List<MessageResourceDto> messageResources;
}