/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwg.centralized.wallet.sessionmanager.controllers;

import com.cwg.centralized.wallet.sessionmanager.Constants.Constants;
import com.cwg.centralized.wallet.sessionmanager.requests.AuthUserRequest;
import com.cwg.centralized.wallet.sessionmanager.responses.BaseResponse;
import com.cwg.centralized.wallet.sessionmanager.services.SessionManagerClientUserService;
import io.jsonwebtoken.Jwts;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author SmartCore Contributors
 */
@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
public class SessionManagerControllers {

    private static final String AUTHENTICATION_SCHEME = "Bearer";
    private static final String INVALID_AUTH_TOKEN = "Your Session Has Expired";

    private final SessionManagerClientUserService sessionManagerService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${gen.jwt.secret-key}")
    private String secretKey;

    @Value("${gen.redis.enable.jwt.black-list}")
    private boolean isJwtBlackListitingEnabled;

    @ApiOperation(value = "Authenticate-Admin User, This API is dev for Administrative Login. The API are consumed by all channels {Web, Mobile, Api}.", tags = "Session managers Services")

    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success code, Description"),
        @ApiResponse(code = 400, message = "Validation Error code"),
        @ApiResponse(code = 201, message = "Accepted for processing"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 500, message = "Server end exception"),
        @ApiResponse(code = 404, message = "Resource not available")

    })
    @PostMapping("/authenticate/user")
    public ResponseEntity<BaseResponse> authenticateAdmin(@RequestBody AuthUserRequest rq, @RequestHeader(value = "channel", required = true) String channel,
            HttpServletRequest request) {
        return sessionManagerService.authenticateUser(rq, request, channel);
    }

    @ApiOperation(value = "Logout-Users, This API is dev to logout all Users. The API are consumed by all channels {Web, Mobile Api}.", tags = "Session managers Services")

    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success code, Description"),
        @ApiResponse(code = 400, message = "Validation Error code"),
        @ApiResponse(code = 201, message = "Accepted for processing"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 500, message = "Server end exception"),
        @ApiResponse(code = 404, message = "Resource not available")

    })
    @GetMapping("/logout")
    public ResponseEntity<BaseResponse> issueToken(@RequestHeader("Authorization") String header) {
        return sessionManagerService.destroyJwt(header);
    }

    @GetMapping("/verify")
    public ResponseEntity<BaseResponse> validateToken(@RequestHeader("Authorization") String header) {
        BaseResponse response = new BaseResponse();

        if (!isTokenBasedAuthentication(header)) {
            response.setDescription(INVALID_AUTH_TOKEN);
            response.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
            return ResponseEntity.ok(response);
        }

        String token = header.substring(AUTHENTICATION_SCHEME.length()).trim();
        if (validateTokenValue(token)) {
            response.setDescription(Constants.AUTHENTICATION_VALID);
            response.setStatusCode(HttpServletResponse.SC_OK);
            return ResponseEntity.ok(response);
        }

        response.setDescription(INVALID_AUTH_TOKEN);
        response.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
        return ResponseEntity.ok(response);
    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {
        return authorizationHeader != null
                && authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private boolean validateTokenValue(String jwt) {
        try {
            if (isJwtBlackListitingEnabled) {
                String token = (String) redisTemplate.opsForValue().get(jwt);
                if (token != null) {
                    return false;
                }
            }
        } catch (Exception ex) {
            return false;
        }

        try {
            Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(secretKey)).parseClaimsJws(jwt).getBody();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}
