package com.cheil.form;

import com.cheil.core.mongo.model.Node;
import com.cheil.core.mongo.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class RoleForm extends BaseForm {
    private String name;
    private Boolean published;
    private Set<User> users;
    private Set<Node> permissions;
    private String quota;
    private String quotaUsed;
    private List<RoleForm> roleFormList;
}
