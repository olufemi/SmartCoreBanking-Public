/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwg.centralized.wallet.utility.util;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.common.collect.Range;
import com.google.gson.Gson;

import java.security.InvalidKeyException;
import java.security.Key;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.joda.time.DateTime;
import org.joda.time.Years;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author SmartCore Contributors
 */
@Service
public class UttilityMethods {

    MemoryCache cache;
    String SETTING_KEY_CHANNELS_LIST_WALLET_AGENT_REG;
    String SETTING_KEY_GROUP_TYEPE_LIST;
    String SEETING_ONE_TIME_PWD_EXP_MINS;
    String SEETING_TOKEN_EXPIRED_RES_CODE;
    static String DB_TIMESTAMP;
    public String REQUEST_WINDOW_TIME_IN_MINUTES;
    public String MIN_REG_AGE;
    public String CHECK_BVN_IS_IN_USE;
    String SETTING_KEY_GET_WALLET_AS_SIMPLE;
    String SETTING_KEY_WAL_EX_DAYS;
    String SETTING_KEY_GET_WALLET_USER_GROUP_ID;
    String SETTING_KEY_GET_IS_BVN_VALIDATION_ENABLED;
    String SETTING_KEY_GET_TIER_1;
    String SETTING_KEY_GET_TIER_2;
    String SETTING_KEY_GET_TIER_3;
    String SETTING_KEY_GET_TIER_4;
    @Value("${spring.profiles.active}")
    private String activeProfile;
    String SETTING_KEY_PK_PAYSTACK;
    String SETTING_KEY_MONTY_SMS;
    String SETTING_KEY_MONTY_SEND_ID;
    String SETTING_KEY_WEB_HOOK_KEY;
    String SETTING_KEY_WEB_SEND_GRID;
    String SETTING_KEY_FINCRA_SUB_ACCOUNT_URL;
    String SETTING_KEY_FINCRA_MAIN_ACCOUNT_ID;
    String SETTING_KEY_PK_FINCRA;
    String SETTING_DEVICE_LIM_CHECK_PERIOD;

    String SETTING_PUSH_NOTIFY_ACCESS_TOKEN;
    String SETTING_PUSH_NOTIFY_REQUEST_URL;
    String SETTING_PAYOUT_PROVIDER;
    String SETTING_KEY_VIRTUAL_ACCOUNT_PROVIDER;
    String SETTING_KEY_PRIVD_CLIENT_ID;
    String SETTING_KEY_PRIVD_X_Auth_Signature;
    String SETTING_KEY_PRIVD_VIRT_BASE_PAYMENT_URL;
    String SETTING_KEY_PRIVD_VIRT_BASE_URL;
    String SETTING_KEY_USERNAME;
    String SETTING_KEY_PASSWORD;
    String SETTING_KEY_SMS_SERVICE_VENDOR;
    String SETTING_KEY_TWILLO_SID;
    String SETTING_KEY_TWILLO_TOK;
    String SETTING_KEY_TWILLO_PHN_NUMB;

    private final Logger logger = LoggerFactory.getLogger(UttilityMethods.class);

    public UttilityMethods(MemoryCache cache) {
        this.cache = cache;

    }

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(6);
    }

    @PostConstruct
    public void init() {
        SETTING_KEY_GROUP_TYEPE_LIST = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_GROUP_TYEPE_LIST);
        SETTING_KEY_CHANNELS_LIST_WALLET_AGENT_REG = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_CHANELS_LIST);
        SEETING_ONE_TIME_PWD_EXP_MINS = cache.getApplicationSetting(AppConfigConUtil.SEETING_ONE_TIME_PWD_EXP_MINS);
        SEETING_TOKEN_EXPIRED_RES_CODE = cache.getApplicationSetting(AppConfigConUtil.SEETING_TOKEN_EXPIRED_RES_CODE);
        DB_TIMESTAMP = cache.getApplicationSetting(AppConfigConUtil.DB_TIMESTAMP);
        REQUEST_WINDOW_TIME_IN_MINUTES = cache.getApplicationSetting(AppConfigConUtil.REQUEST_WINDOW_TIME_IN_MINUTES);
        MIN_REG_AGE = cache.getApplicationSetting(AppConfigConUtil.MIN_REG_AGE);
        CHECK_BVN_IS_IN_USE = cache.getApplicationSetting(AppConfigConUtil.MIN_REG_AGE);
        SETTING_KEY_GET_WALLET_AS_SIMPLE = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_GET_WALLET_AS_SIMPLE);
        SETTING_KEY_WAL_EX_DAYS = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_WAL_EX_DAYS);
        SETTING_KEY_GET_WALLET_USER_GROUP_ID = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_GET_WALLET_USER_GROUP_ID);
        SETTING_KEY_GET_IS_BVN_VALIDATION_ENABLED = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_GET_IS_BVN_VALIDATION_ENABLED);
        SETTING_KEY_GET_TIER_1 = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_GET_TIER_1);
        SETTING_KEY_GET_TIER_2 = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_GET_TIER_2);
        SETTING_KEY_GET_TIER_3 = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_GET_TIER_3);
        SETTING_KEY_GET_TIER_4 = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_GET_TIER_4);
        SETTING_KEY_PK_PAYSTACK = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_PK_PAYSTACK);
        SETTING_KEY_MONTY_SMS = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_MONTY_SMS);
        SETTING_KEY_MONTY_SEND_ID = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_MONTY_SMS);
        SETTING_KEY_WEB_HOOK_KEY = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_WEB_HOOK_KEY);
        SETTING_KEY_WEB_SEND_GRID = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_WEB_SEND_GRID);
        SETTING_KEY_FINCRA_SUB_ACCOUNT_URL = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_FINCRA_SUB_ACCOUNT_URL);
        SETTING_KEY_FINCRA_MAIN_ACCOUNT_ID = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_FINCRA_MAIN_ACCOUNT_ID);
        SETTING_KEY_PK_FINCRA = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_PK_FINCRA);
        SETTING_DEVICE_LIM_CHECK_PERIOD = cache.getApplicationSetting(AppConfigConUtil.SETTING_DEVICE_LIM_CHECK_PERIOD);
        SETTING_PUSH_NOTIFY_ACCESS_TOKEN = cache.getApplicationSetting(AppConfigConUtil.SETTING_PUSH_NOTIFY_ACCESS_TOKEN);
        SETTING_PUSH_NOTIFY_REQUEST_URL = cache.getApplicationSetting(AppConfigConUtil.SETTING_PUSH_NOTIFY_REQUEST_URL);
        SETTING_PAYOUT_PROVIDER = cache.getApplicationSetting(AppConfigConUtil.SETTING_PAYOUT_PROVIDER);
        SETTING_KEY_VIRTUAL_ACCOUNT_PROVIDER = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_VIRTUAL_ACCOUNT_PROVIDER);
        SETTING_KEY_PRIVD_CLIENT_ID = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_PRIVD_CLIENT_ID);
        SETTING_KEY_PRIVD_X_Auth_Signature = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_PRIVD_X_AUTH_SIG);
        SETTING_KEY_PRIVD_VIRT_BASE_PAYMENT_URL = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_PRIVD_VIRT_BASE_PAYMENT_URL);
        SETTING_KEY_PRIVD_VIRT_BASE_URL = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_PRIVD_VIRT_BASE_URL);
        SETTING_KEY_USERNAME = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_USERNAME);
        SETTING_KEY_PASSWORD = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_PASSWORD);
        SETTING_KEY_SMS_SERVICE_VENDOR = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_SMS_SERVICE_VENDOR);
        SETTING_KEY_TWILLO_SID = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_TWILLO_SID);
        SETTING_KEY_TWILLO_TOK = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_TWILLO_TOK);
        SETTING_KEY_TWILLO_PHN_NUMB = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_TWILLO_PHN_NUMB);

    }

    public String getSETTING_KEY_TWILLO_PHN_NUMB() {
        return SETTING_KEY_TWILLO_PHN_NUMB;
    }

    public String getSETTING_KEY_TWILLO_TOK() {
        return SETTING_KEY_TWILLO_TOK;
    }

    public String getSETTING_KEY_TWILLO_SID() {
        return SETTING_KEY_TWILLO_SID;
    }

    public String getSETTING_KEY_SMS_SERVICE_VENDOR() {
        return SETTING_KEY_SMS_SERVICE_VENDOR;
    }

    public String getSETTING_KEY_USERNAME() {
        return SETTING_KEY_USERNAME;
    }

    public String getSETTING_KEY_PASSWORD() {
        return SETTING_KEY_PASSWORD;
    }

    public String getSETTING_KEY_PRIVD_VIRT_BASE_PAYMENT_URL() {
        return SETTING_KEY_PRIVD_VIRT_BASE_PAYMENT_URL;
    }

    public String getSETTING_KEY_PRIVD_VIRT_BASE_URL() {
        return SETTING_KEY_PRIVD_VIRT_BASE_URL;
    }

    public String getSETTING_KEY_PRIVD_X_Auth_Signature() {
        return SETTING_KEY_PRIVD_X_Auth_Signature;
    }

    public String getSETTING_KEY_PRIVD_CLIENT_ID() {
        return SETTING_KEY_PRIVD_CLIENT_ID;
    }

    public String getSETTING_KEY_VIRTUAL_ACCOUNT_PROVIDER() {
        return SETTING_KEY_VIRTUAL_ACCOUNT_PROVIDER;
    }

    public String getSETTING_PAYOUT_PROVIDER() {
        return SETTING_PAYOUT_PROVIDER;
    }

    public String getSETTING_PUSH_NOTIFY_ACCESS_TOKEN() {
        return SETTING_PUSH_NOTIFY_ACCESS_TOKEN;
    }

    public String getSETTING_PUSH_NOTIFY_REQUEST_URL() {
        return SETTING_PUSH_NOTIFY_REQUEST_URL;
    }

    public String getSETTING_DEVICE_LIM_CHECK_PERIOD() {
        return SETTING_DEVICE_LIM_CHECK_PERIOD;
    }

    public String frinaPolicy() {
        return SETTING_KEY_PK_FINCRA;
    }

    public String SETTING_KEY_FINCRA_SUB_ACCOUNT_URL() {
        return SETTING_KEY_FINCRA_SUB_ACCOUNT_URL;
    }

    public String SETTING_KEY_FINCRA_MAIN_ACCOUNT_ID() {
        return SETTING_KEY_FINCRA_MAIN_ACCOUNT_ID;
    }

    public String getWebHookKey() {
        return SETTING_KEY_WEB_HOOK_KEY;
    }

    public String SETTING_KEY_WEB_SEND_GRID() {
        return SETTING_KEY_WEB_SEND_GRID;
    }

    public String getMontySeriveId() {
        return SETTING_KEY_MONTY_SEND_ID;
    }

    public String getMontySMS() {
        return SETTING_KEY_MONTY_SMS;
    }

    public String pkPayStack() {
        return SETTING_KEY_PK_PAYSTACK;
    }

    public String getTier1() {
        return SETTING_KEY_GET_TIER_1;
    }

    public String getTier2() {
        return SETTING_KEY_GET_TIER_2;
    }

    public String getTier3() {
        return SETTING_KEY_GET_TIER_3;
    }

    public String getTier4() {
        return SETTING_KEY_GET_TIER_4;
    }

    public String getTokenExpiredResCode() {
        return SEETING_TOKEN_EXPIRED_RES_CODE;
    }

    public String isValidateBvnEnabled() {
        return SETTING_KEY_GET_IS_BVN_VALIDATION_ENABLED;
    }

    public String getoneTimePwdExpireMins() {
        return SEETING_ONE_TIME_PWD_EXP_MINS;
    }

    public String returnWalletUserGroupId() {

        return SETTING_KEY_GET_WALLET_USER_GROUP_ID;

    }

    //(0/91)?[7-9][0-9]{9}
    private final Pattern Check10Digits = Pattern.compile("\\d{11}");
    //(0/91)?[7-9][0-9]{9}
    //private final Pattern Check11Digits = Pattern.compile("\\d{12}");
    private final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    public boolean isValid11Num(String strNum) {
        if (strNum == null) {
            return false;
        }
        return Check10Digits.matcher(strNum).matches();
    }
    private final Pattern CheckEmailAdd = Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");

    public boolean isValidEmailAddress(String email) {
        if (email == null) {
            return false;
        }
        return CheckEmailAdd.matcher(email).matches();
    }

    private final Pattern CheckAcctDigits = Pattern.compile("\\d{10}");

    public boolean isValidBankAcctNumb(String strNum) {
        if (strNum == null) {
            return false;
        }
        return CheckAcctDigits.matcher(strNum).matches();
    }

    public boolean isNumeric(String strNum) {

        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    public List<String> getChannelList() {
        List<String> convertedSETTING_KEY_CHANNELS_LIST = Stream.of(SETTING_KEY_CHANNELS_LIST_WALLET_AGENT_REG.split(",", -1))
                .collect(Collectors.toList());
        return convertedSETTING_KEY_CHANNELS_LIST;
    }

    public List<String> getGroupTypesList() {
        List<String> convertedSETTING_KEY_GROUPLIST_LIST = Stream.of(SETTING_KEY_GROUP_TYEPE_LIST.split(",", -1))
                .collect(Collectors.toList());
        return convertedSETTING_KEY_GROUPLIST_LIST;
    }

    public String encryptPass(String toEncode) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(toEncode);
        return encodedPassword;
    }

    public boolean getIfChannelExist(String channel) {
        // logger.info(String.format("channel >>>>>>=>%s", channel));

        //Before saving
        //Get list of channels and interate then check against caller-channel to validate
        List<String> channels = getChannelList();
        //   logger.info(String.format("cutilMeth.getChannelList(); >>>>>>=>%s", channels));
        boolean setChannelToF = false;
        // boolean checkOption = false;
        for (String channelType : channels) {
            //   logger.info(String.format("channelType >>>>>>=>%s", channelType));
            if (channel.equals(channelType)) {
                setChannelToF = true;
            }
        }
        return setChannelToF;

    }

    public boolean getIfGroupTypeExist(String channel) {
        logger.info(String.format("channel >>>>>>=>%s", channel));

        //Before saving
        //Get list of channels and interate then check against caller-channel to validate
        List<String> groupTypes = getGroupTypesList();
        logger.info(String.format("cutilMeth.getGroupTypesList(); >>>>>>=>%s", groupTypes));
        boolean setGroupTypeToF = false;
        // boolean checkOption = false;
        for (String groupType : groupTypes) {
            logger.info(String.format("groupType >>>>>>=>%s", groupType));
            if (channel.equals(groupType)) {
                setGroupTypeToF = true;
            }
        }
        return setGroupTypeToF;

    }

    public String cntrlReTimeInMinutes() {
        return REQUEST_WINDOW_TIME_IN_MINUTES;
    }

    public boolean checkIfRequestIsWithinLastReqWindow(String CREATED_DATE, String TIME_IN_MINUTES) {

        logger.info(String.format("returning from checkIfRequestIsWithinLastReqWindow >>>>>>=>CREATED_DATE::: %s   TIME_IN_MINUTES::: %s", CREATED_DATE, TIME_IN_MINUTES));

        LocalDateTime now = LocalDateTime.now();
        String returnValue = String.format("now:: %s within Given Closed Range[getStartLocalDate::: %s, getEndLocalDate::: %s] returns %s",
                now.plusSeconds(2).toString(DateTimeFormat.forPattern(DB_TIMESTAMP)),
                getStartLocalDate(CREATED_DATE).toString(DB_TIMESTAMP), getEndLocalDate(CREATED_DATE, TIME_IN_MINUTES).toString(DB_TIMESTAMP),
                dateWithinMins(now, CREATED_DATE, TIME_IN_MINUTES));

        logger.info(String.format("checkIfRequestIsWithinLastReqWindow supplied mechanism>>>>>>=>%s", returnValue));
        logger.info(String.format("returning from checkIfRequestIsWithinLastReqWindow >>>>>>=>%s", dateWithinMins(now.plusSeconds(2), CREATED_DATE, TIME_IN_MINUTES)));
        return dateWithinMins(now.plusSeconds(2), CREATED_DATE, TIME_IN_MINUTES);

    }

    private static boolean dateWithinMins(org.joda.time.LocalDateTime now, String CREATED_DATE, String TIME_IN_MINUTES) {
        return Range.closed(getStartLocalDate(CREATED_DATE), getEndLocalDate(CREATED_DATE, TIME_IN_MINUTES)).contains(now);
    }

    static LocalDateTime getStartLocalDate(String CREATED_DATE) {

        return LocalDateTime.parse(CREATED_DATE, DateTimeFormat.forPattern(DB_TIMESTAMP));
    }

    static LocalDateTime getEndLocalDate(String CREATED_DATE, String TIME_IN_MINUTES) {
        return getStartLocalDate(CREATED_DATE).plusMinutes(Integer.parseInt(TIME_IN_MINUTES));
    }

    private final Pattern formatDateOfBirth = Pattern.compile("^(0?[1-9]|[12][0-9]|3[01])[\\/](0?[1-9]|1[012])[\\/]\\d{4}$");

    public boolean formatDateOfBirth(String ddd) {
        if (ddd == null) {
            return false;
        }
        return formatDateOfBirth.matcher(ddd).matches();

    }

    public String getMinRegAge() {
        return MIN_REG_AGE;
    }

    public boolean hasAgeRequirement(DateTime userDob, int minimumAge) {
        DateTime now = new DateTime();
        Years age = Years.yearsBetween(userDob, now);
        return age.getYears() >= minimumAge;
    }
    private final Pattern CheckBVNDigits = Pattern.compile("\\d{10}");

    public boolean isValidBVNNumb(String strNum) {
        if (strNum == null) {
            return false;
        }
        return CheckBVNDigits.matcher(strNum).matches();
    }

    public boolean checkIfCheckBVNInUse() {
        boolean isInUse = false;
        if (CHECK_BVN_IS_IN_USE.equals("1")) {
            isInUse = true;
        }

        return isInUse;

    }

    public String returnWalletSimpleType() {

        return SETTING_KEY_GET_WALLET_AS_SIMPLE;

    }

    public String getWalletPinExPDays() {
        return SETTING_KEY_WAL_EX_DAYS;

    }

    private final Pattern fourDigitPin = Pattern.compile("\\d{4}");

    public boolean fourDigitPin(String pin) {
        if (pin == null) {
            return false;
        }
        return fourDigitPin.matcher(pin).matches();

    }

    private final static Pattern UUID_REGEX_PATTERN
            = Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");

    public boolean isValidUUID(String str) {
        if (str == null) {
            return false;
        }
        return UUID_REGEX_PATTERN.matcher(str).matches();
    }

    public String genCusId() {

        String fromGlobal = GlobalMethods.generateNUBAN();
        System.out.println(" fromGlobal.substring :::::::: " + "::::: " + "11" + fromGlobal.substring(0, fromGlobal.length() - 3));

        String procFrmGlobal = "11" + fromGlobal.substring(0, fromGlobal.length() - 3);
        System.out.println(" process Frm Global :::::::: " + "::::: " + procFrmGlobal);

        return procFrmGlobal;

    }

    public String genSecureRandum() throws NoSuchAlgorithmException {

        String chrs = "0123456789abcdefghijklmnopqrstuvwxyz";
        SecureRandom secureRandom = SecureRandom.getInstanceStrong();
        // 9 is the length of the string you want
        String customTag = secureRandom.ints(10, 0, chrs.length()).mapToObj(i -> chrs.charAt(i))
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();

        return customTag;
    }

    public String encyrpt(String text, String key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        // Create key and cipher
        Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        //Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        // encrypt the text
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encrypted = cipher.doFinal(text.getBytes());

        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String encrypted, String key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        //Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        // encrypt the text
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        String decrypted = new String(cipher.doFinal(Base64.getDecoder().decode(encrypted)));
        return decrypted;
    }

}
