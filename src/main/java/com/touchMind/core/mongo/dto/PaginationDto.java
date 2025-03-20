package com.touchMind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Sort;


@Getter
@Setter
@NoArgsConstructor
@ToString
public class PaginationDto {
    private int page;
    private int sizePerPage = 50;
    private String sortDirection = Sort.Direction.DESC.toString();
    private String sortField = "identifier";
    private int totalPages;
    private long totalRecords;
}
