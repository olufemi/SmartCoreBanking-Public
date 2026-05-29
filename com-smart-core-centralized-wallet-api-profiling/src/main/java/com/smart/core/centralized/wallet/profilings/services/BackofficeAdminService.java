package com.smart.core.centralized.wallet.profilings.services;

import com.smart.core.centralized.wallet.profilings.domains.BackofficeRole;
import com.smart.core.centralized.wallet.profilings.domains.BackofficeUser;
import com.smart.core.centralized.wallet.profilings.model.BackofficePasswordResetRequest;
import com.smart.core.centralized.wallet.profilings.model.BackofficeRoleRequest;
import com.smart.core.centralized.wallet.profilings.model.BackofficeUserRequest;
import com.smart.core.centralized.wallet.profilings.model.BaseResponse;
import com.smart.core.centralized.wallet.profilings.repo.BackofficeRoleRepo;
import com.smart.core.centralized.wallet.profilings.repo.BackofficeUserRepo;
import com.smart.core.centralized.wallet.profilings.utils.UttilityMethods;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class BackofficeAdminService {

    private final BackofficeRoleRepo roleRepo;
    private final BackofficeUserRepo userRepo;
    private final UttilityMethods utilityMethods;

    @Value("${smartcore.backoffice.admin.api-key:}")
    private String adminApiKey;

    public BackofficeAdminService(BackofficeRoleRepo roleRepo, BackofficeUserRepo userRepo, UttilityMethods utilityMethods) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.utilityMethods = utilityMethods;
    }

    public BaseResponse createRole(String adminKey, String actor, BackofficeRoleRequest request) {
        BaseResponse auth = authorize(adminKey);
        if (auth != null) {
            return auth;
        }
        if (roleRepo.existsByRoleCode(normalize(request.getRoleCode()))) {
            return fail(409, "Backoffice role already exists.");
        }

        BackofficeRole role = new BackofficeRole();
        role.setRoleCode(normalize(request.getRoleCode()));
        role.setRoleName(request.getRoleName().trim());
        role.setPermissions(joinPermissions(request.getPermissions()));
        role.setStatus("ACTIVE");
        role.setCreatedBy(firstNonBlank(actor, "SYSTEM"));
        role.setCreatedAt(Instant.now());
        role.setUpdatedAt(Instant.now());
        roleRepo.save(role);

        return ok(role, "Backoffice role created.");
    }

    public BaseResponse listRoles(String adminKey) {
        BaseResponse auth = authorize(adminKey);
        if (auth != null) {
            return auth;
        }
        return ok(roleRepo.findAll(), "Backoffice roles fetched.");
    }

    public BaseResponse createUser(String adminKey, String actor, BackofficeUserRequest request) {
        BaseResponse auth = authorize(adminKey);
        if (auth != null) {
            return auth;
        }
        String roleCode = normalize(request.getRoleCode());
        if (!roleRepo.findByRoleCode(roleCode).isPresent()) {
            return fail(400, "Backoffice role does not exist.");
        }
        if (userRepo.existsByOperatorId(normalize(request.getOperatorId()))) {
            return fail(409, "Backoffice operator already exists.");
        }

        BackofficeUser user = new BackofficeUser();
        user.setOperatorId(normalize(request.getOperatorId()));
        user.setEmailAddress(request.getEmailAddress().trim().toLowerCase());
        user.setFullName(request.getFullName().trim());
        user.setProductCode(normalize(request.getProductCode()));
        user.setRoleCode(roleCode);
        user.setStatus("ACTIVE");
        if (StringUtils.hasText(request.getPassword())) {
            user.setPasswordHash(utilityMethods.encryptPass(request.getPassword().trim()));
            user.setPasswordSetAt(Instant.now());
        }
        user.setCreatedBy(firstNonBlank(actor, "SYSTEM"));
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        userRepo.save(user);

        return ok(user, "Backoffice operator created.");
    }

    public BaseResponse setUserPassword(String adminKey, String actor, BackofficePasswordResetRequest request) {
        BaseResponse auth = authorize(adminKey);
        if (auth != null) {
            return auth;
        }
        Optional<BackofficeUser> userOpt = userRepo.findByOperatorId(normalize(request.getOperatorId()));
        if (!userOpt.isPresent()) {
            return fail(404, "Backoffice operator not found.");
        }
        BackofficeUser user = userOpt.get();
        user.setPasswordHash(utilityMethods.encryptPass(request.getPassword().trim()));
        user.setPasswordSetAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        user.setCreatedBy(firstNonBlank(actor, user.getCreatedBy()));
        userRepo.save(user);
        return ok(user, "Backoffice operator password updated.");
    }

    public BaseResponse listUsers(String adminKey, String productCode) {
        BaseResponse auth = authorize(adminKey);
        if (auth != null) {
            return auth;
        }
        if (StringUtils.hasText(productCode)) {
            return ok(userRepo.findByProductCodeOrderByCreatedAtDesc(normalize(productCode)), "Backoffice operators fetched.");
        }
        return ok(userRepo.findAll(), "Backoffice operators fetched.");
    }

    private BaseResponse authorize(String adminKey) {
        if (!StringUtils.hasText(adminApiKey)) {
            return null;
        }
        if (!adminApiKey.equals(adminKey)) {
            return fail(401, "Invalid backoffice admin key.");
        }
        return null;
    }

    private BaseResponse ok(Object data, String description) {
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setDescription(description);
        response.addData("result", data);
        return response;
    }

    private BaseResponse fail(int statusCode, String description) {
        BaseResponse response = new BaseResponse();
        response.setStatusCode(statusCode);
        response.setDescription(description);
        return response;
    }

    private String joinPermissions(List<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return "";
        }
        Collections.sort(permissions);
        return String.join(",", permissions);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }

    private String firstNonBlank(String first, String fallback) {
        return StringUtils.hasText(first) ? first : fallback;
    }
}
