package com.smart.core.centralized.wallet.profilings.services;

import com.smart.core.centralized.wallet.profilings.domains.BackofficeRole;
import com.smart.core.centralized.wallet.profilings.model.BackofficePortalSession;
import com.smart.core.centralized.wallet.profilings.domains.BackofficeUser;
import com.smart.core.centralized.wallet.profilings.model.BackofficePortalLoginRequest;
import com.smart.core.centralized.wallet.profilings.model.BaseResponse;
import com.smart.core.centralized.wallet.profilings.repo.BackofficeRoleRepo;
import com.smart.core.centralized.wallet.profilings.repo.BackofficeUserRepo;
import com.smart.core.centralized.wallet.profilings.utils.UttilityMethods;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class BackofficePortalAuthService {

    private final BackofficeUserRepo userRepo;
    private final BackofficeRoleRepo roleRepo;
    private final UttilityMethods utilityMethods;

    @Value("${smartcore.backoffice.portal.jwt-secret:${SMARTCORE_BACKOFFICE_PORTAL_JWT_SECRET}}")
    private String portalJwtSecret;

    @Value("${smartcore.backoffice.portal.jwt-expiration-minutes:480}")
    private long portalJwtExpirationMinutes;

    public BackofficePortalAuthService(BackofficeUserRepo userRepo,
                                       BackofficeRoleRepo roleRepo,
                                       UttilityMethods utilityMethods) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.utilityMethods = utilityMethods;
    }

    public BaseResponse login(BackofficePortalLoginRequest request) {
        if (request == null || !StringUtils.hasText(request.getEmailAddress()) || !StringUtils.hasText(request.getPassword())) {
            return fail(400, "emailAddress and password are required.");
        }
        Optional<BackofficeUser> userOpt = userRepo.findFirstByEmailAddressIgnoreCase(request.getEmailAddress().trim().toLowerCase());
        if (!userOpt.isPresent()) {
            return fail(401, "Invalid portal credentials.");
        }
        BackofficeUser user = userOpt.get();
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            return fail(403, "Backoffice operator is not active.");
        }
        if (!StringUtils.hasText(user.getPasswordHash())) {
            return fail(403, "Portal password is not set for this operator.");
        }
        if (!utilityMethods.passwordEncoder().matches(request.getPassword().trim(), user.getPasswordHash())) {
            return fail(401, "Invalid portal credentials.");
        }
        return ok(buildSession(user), "Portal login successful.");
    }

    public BaseResponse me(String authorization) {
        try {
            BackofficePortalSession session = requireSession(authorization);
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("operatorId", session.getOperatorId());
            data.put("emailAddress", session.getEmailAddress());
            data.put("fullName", session.getFullName());
            data.put("productCode", session.getProductCode());
            data.put("roleCode", session.getRoleCode());
            data.put("permissions", session.getPermissions());
            data.put("scope", session.getScope());
            return ok(data, "Portal session fetched.");
        } catch (Exception ex) {
            return fail(401, "Portal session is invalid or expired.");
        }
    }


    public BackofficePortalSession requireSession(String authorization) {
        Claims claims = parseClaims(authorization);
        BackofficePortalSession session = new BackofficePortalSession();
        session.setOperatorId(String.valueOf(claims.get("operatorId")));
        session.setEmailAddress(String.valueOf(claims.get("emailAddress")));
        session.setFullName(String.valueOf(claims.get("fullName")));
        session.setProductCode(String.valueOf(claims.get("productCode")));
        session.setRoleCode(String.valueOf(claims.get("roleCode")));
        session.setPermissions(String.valueOf(claims.get("permissions")));
        session.setScope(String.valueOf(claims.get("scope")));
        return session;
    }


    private Map<String, Object> buildSession(BackofficeUser user) {
        Optional<BackofficeRole> role = roleRepo.findByRoleCode(user.getRoleCode());
        String permissions = role.isPresent() ? role.get().getPermissions() : "";
        String scope = "ALL".equalsIgnoreCase(user.getProductCode()) ? "INTERNAL" : "CLIENT";
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(portalJwtExpirationMinutes * 60L);

        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("operatorId", user.getOperatorId());
        claims.put("emailAddress", user.getEmailAddress());
        claims.put("fullName", user.getFullName());
        claims.put("productCode", user.getProductCode());
        claims.put("roleCode", user.getRoleCode());
        claims.put("permissions", permissions);
        claims.put("scope", scope);

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject("BackofficePortal")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(SignatureAlgorithm.HS256, signingKey())
                .compact();

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("token", token);
        data.put("tokenType", "Bearer");
        data.put("expiresAt", expiry.toString());
        data.putAll(claims);
        return data;
    }

    private Claims parseClaims(String authorization) {
        String token = authorization;
        if (StringUtils.hasText(token) && token.toLowerCase().startsWith("bearer ")) {
            token = token.substring(7).trim();
        }
        return Jwts.parser().setSigningKey(signingKey()).parseClaimsJws(token).getBody();
    }


    private byte[] signingKey() {
        try {
            return MessageDigest.getInstance("SHA-256").digest(portalJwtSecret.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to derive portal signing key.", ex);
        }
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
}
