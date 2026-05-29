package com.smart.core.centralized.wallet.profilings.model;

import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BackofficeRoleRequest {

    @NotBlank(message = "the field \"roleCode\" is required")
    private String roleCode;

    @NotBlank(message = "the field \"roleName\" is required")
    private String roleName;

    private List<String> permissions;
}
