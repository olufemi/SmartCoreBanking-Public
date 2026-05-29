package com.cwg.centralized.wallet.sessionmanager.services;

import com.cwg.centralized.wallet.sessionmanager.entities.SessionServiceLog;
import com.cwg.centralized.wallet.sessionmanager.exceptions.CustomApplicationException;
import com.cwg.centralized.wallet.sessionmanager.proxy.UtilityServiceFeignService;
import com.cwg.centralized.wallet.sessionmanager.repository.AuthenticationLogRepository;
import com.cwg.centralized.wallet.sessionmanager.requests.AuthUserRequest;
import com.cwg.centralized.wallet.sessionmanager.requests.EmailRequestDemo;
import com.cwg.centralized.wallet.sessionmanager.requests.UserDeviceRequest;
import com.cwg.centralized.wallet.sessionmanager.responses.AuthApiResponse;
import com.cwg.centralized.wallet.sessionmanager.responses.BaseResponse;
import com.cwg.centralized.wallet.sessionmanager.utils.DecodedToken;
import com.google.gson.Gson;
import java.security.Key;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.concurrent.TimeUnit;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import lombok.RequiredArgsConstructor;
import org.joda.time.DateTimeComparator;

@Service
@RequiredArgsConstructor
public class SessionManagerClientUserService {

    private Logger logger = LoggerFactory.getLogger(SessionManagerClientUserService.class);

    private static final String TOKEN = "idToken";
    private static final String ISSUER = "NEXTGEN";
    private static final String SUBJECT = "Authentication";
    private static final String LOGIN_SUCCESSFUL = "Login Successful";
    private static final int LOGIN_STATUS_CODE_1 = 200;
    private static final int LOGIN_STATUS_CODE_75 = 75;

    private static final String AUTHENTICATION_SCHEME = "Bearer";

    private static final String HTTP_PROTOCOL = "http://";

    @Value("${gen.jwt.secret-key}")
    private String secretKey;

    @Value("${gen.exempt.uuids}")
    private String uuids;

    @Value("${gen.jwt.expiration-period}")
    private long tokenExpiration;

    @Value("${gen.redis.enable.jwt.black-list}")
    private boolean isJwtBlackListitingEnabled;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final AuthenticationLogRepository authenticationLogRepository;

    @Autowired
    private final UtilityServiceFeignService UtilityServiceFeignService;

    @Autowired
    private UtilityServiceFeignService utilityServiceFeignService;

    public ResponseEntity<BaseResponse> authenticateUser(AuthUserRequest rq, HttpServletRequest request, String channel) {

        BaseResponse baseResponse = new BaseResponse();

        SessionServiceLog log = new SessionServiceLog();

        String loginIP = getClientIpAddr(request);

        if (rq.getEmailAddress() == null) {
            baseResponse.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
            baseResponse.setDescription("Email Address cannot be null!");
        }

        if (rq.getPassword() == null) {
            baseResponse.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
            baseResponse.setDescription("Password cannot be null!");
        }

        log.setLogIP(loginIP);
        log.setCreatedDate(Instant.now());

        try {
            
            BaseResponse response = UtilityServiceFeignService.authenticateUser(rq, channel);

            logger.info(String.format("util auth response >>>>>> +++++++++++++ =>%s", response));

            if (response.getStatusCode() == LOGIN_STATUS_CODE_1) {

                String email = (String) response.getData().get("emailAddress");

                String productName = (String) response.getData().get("productName");
                String productCode = (String) response.getData().get("productCode");

                AuthApiResponse res = new AuthApiResponse();

                res.setProductCode(productCode);
                res.setProductName(productName);
                res.setEmailAddress(email);
                // BaseResponse baseResponse2 = new BaseResponse();
                baseResponse.setStatusCode(200);
                baseResponse.addData("enabled", "1");

                // baseResponse2 = utilityServiceFeignService.checkIfDeviceBelongsToUser(userDeviceRequest,channel);
                if (baseResponse.getStatusCode() == HttpServletResponse.SC_OK) {
                    issueToken(baseResponse, rq.getEmailAddress(), res, loginIP);
                    log.setUserId(rq.getEmailAddress().toLowerCase().trim());

                    log.setCreatedDate(Instant.now());
                    log.setMethod("Authentication-User");
                    // log.setCustomerType("Wallet");
                    log.setChannel(channel);

                } else {
                    baseResponse.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
                    baseResponse.setDescription(response.getDescription());
                }
                // }
            } else {
                baseResponse.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
                baseResponse.setDescription(response.getDescription());
            }

        } catch (Exception e) {
            // log.setExceptions(("Exception Occurred " + e.getMessage()).substring(0, 100));
            authenticationLogRepository.save(log);
            e.printStackTrace();
            // logger.error(e.getMessage());

            // propagate exception to return proper message to the user
            throw new CustomApplicationException(HttpStatus.UNAUTHORIZED, "Authentication failed. Please try again");
        }

        log.setApiResponse(baseResponse.getDescription());

        authenticationLogRepository.save(log);
        logger.info(String.format("session manager response >>>>>> +++++++++++++ =>%s", baseResponse));

        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    public ResponseEntity<BaseResponse> destroyJwt(String authorizationHeader) {
        BaseResponse baseResponse = new BaseResponse();
        if (isJwtBlackListitingEnabled) {
            String jwt = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();
            String token = (String) redisTemplate.opsForValue().get(jwt);
            if (token == null) {
                DecodedToken tokenObject;
                try {
                    tokenObject = DecodedToken.getDecoded(authorizationHeader);
                    redisTemplate.opsForValue().set(jwt, tokenObject.userId);
                    redisTemplate.expire(jwt, (int) TimeUnit.MILLISECONDS.toSeconds(tokenObject.exp), TimeUnit.SECONDS);
                    baseResponse.setDescription("Token Successfully Invalidated");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        baseResponse.setStatusCode(HttpServletResponse.SC_OK);
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    private String issueToken(BaseResponse baseResponse, String emailAddress, AuthApiResponse response) {
        Date expire = generateTokenExpiration();
        String token = createJWT(response.getEmailAddress(), response.getProductName(), response.getProductCode(),
                ISSUER, SUBJECT, expire, response.getEmailAddress());
        return token;
    }

    private String createJWT(String userId, String productName, String productCode,
            String issuer,
            String subject, Date expire,
            String email) {

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        Map<String, Object> claims = new HashMap<String, Object>();

        claims.put("email", email);
        claims.put("productName", productName);
        claims.put("productCode", productCode);
        claims.put("exp", expire.getTime());

        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setId(userId)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signingKey, signatureAlgorithm);

        builder.setExpiration(expire);

        return builder.compact();
    }

    private Date generateTokenExpiration() {
        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(tokenExpiration);
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return date;
    }

    private void issueToken(BaseResponse baseResponse, String userId, AuthApiResponse response, String remoteIp
    ) {
        System.out.println("AuthApiResponse response  ::::::::::::::::               ::::: %S  " + new Gson().toJson(response));

        String token = issueToken(baseResponse, userId, response);
        baseResponse.addData(TOKEN, token);
        baseResponse.addData("productName", response.getProductName());
        baseResponse.addData("productCode", response.getProductCode());
        baseResponse.addData("emailAddress", response.getEmailAddress());

        baseResponse.setStatusCode(HttpServletResponse.SC_OK);
        baseResponse.setDescription(LOGIN_SUCCESSFUL);

        Date currentDate = new Date();
        DateTimeComparator dateTimeComparator = DateTimeComparator.getDateOnlyInstance();
        //here set all cummulative to zeros

        if (!response.getEmailAddress().isEmpty() && isValidEmailAddress(response.getEmailAddress())) {
            String name = response.getProductName();
            EmailRequestDemo emailRe = new EmailRequestDemo();
            emailRe.setBody(generateLoginMsg(name));
            emailRe.setSubject("Centralized Login Notification");
            emailRe.setTo(response.getEmailAddress());
            // BaseResponse sendMail = utilityServiceFeignService.sendUserEmailAndSms(emailRe);
            //   System.out.println("sendMail response:::::::: req" + "   >>>>>>>>>>>>>>>>>> ::::::::::::::::::::: " + new Gson().toJson(sendMail));
            System.out.println("sendMail response:::::::: req" + "   >>>>>>>>>>>>>>>>>> ::::::::::::::::::::: ");

        }
    }

    private String generateLoginMsg(String name) {
        /* String otpMessage = "Dear " + name + ", you just logged in to SmartCore Pay. "
                + "Kinldy call {0000000000} now if you didn't initiate this request.";*/
        Date date = new Date();

        DateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
        String timeWithAMPM = dateFormat.format(new Date());
        System.out.println("Current time in AM/PM: " + timeWithAMPM);

        SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");
        String strDate = formatter.format(date);
        System.out.println("MMMM dd, yyyy: " + strDate);

        SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE");
        String dayOftheWeek = sdf2.format(new Date());
        System.out.println("day Of the Week: " + dayOftheWeek);

        String msg;

        msg = " Hello " + name + ",\n"
                + "You logged into this account from a device:" + "" + ","
                + " " + "" + " at " + timeWithAMPM + " on " + dayOftheWeek + " " + strDate + ". \n"
                + "If this login did not originate from you, please let us know by sending an email "
                + "to demo@example.com. \n"
                + "Alternatively, you can call 0000000000 immediately, Thanks.\n"
                + "\n"
                + "PS. If you did not initiate this request, kindly reply to this email \n"
                + "or write to demo@example.com or call us on 0000000000. \n"
                + "There could have been an attempt to breach your account.";

        return msg;

    }

    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (Exception ex) {
            result = false;
        }
        return result;
    }

    private String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private boolean checkIfDeviceIsExempted(String uuid) {
        /**
         * This was added to adapt customer experience center to exempt check
         * device
         */
        List<String> exemptUUIDs = Arrays.asList(uuids.split("\\s*,\\s*"));

        return exemptUUIDs.contains(uuid);

    }

}
