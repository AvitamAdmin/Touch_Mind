package com.touchmind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document("role")
@Getter
@Setter
@NoArgsConstructor
public class Role extends CommonFields {
    @DBRef(lazy = true)
    private Set<Node> permissions;
    private String quota;
    private String quotaUsed;
    private Boolean published;
}
