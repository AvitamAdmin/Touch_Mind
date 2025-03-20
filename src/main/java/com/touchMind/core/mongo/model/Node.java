package com.touchMind.core.mongo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document("Node")
@Getter
@Setter
public class Node extends CommonFields implements Serializable {

    private String path;

    @DBRef
    private Node parentNode;

    private Integer displayPriority = 1000;
}
