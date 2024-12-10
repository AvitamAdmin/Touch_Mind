package com.cheil.core.mongo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Getter
@Setter
public class CommonQaFields extends CommonFields implements Serializable {

    @JsonIgnore
    private String creator;
    @JsonIgnore
    private Boolean status;
    @JsonIgnore
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date creationTime;
    @JsonIgnore
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date lastModified;
    @JsonIgnore
    private String modifier;
    private String identifier;
    private String shortDescription;
    @DBRef(lazy = true)
    private List<ParamInput> paramInput;
    private List<String> subsidiaries;
}
