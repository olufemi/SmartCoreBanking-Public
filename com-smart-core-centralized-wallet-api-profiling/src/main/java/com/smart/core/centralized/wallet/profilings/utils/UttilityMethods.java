/*
 To change this license header, choose License Headers in Project Properties.
 To change this template file, choose Tools | Templates
 and open the template in the editor.
 */
package com.smart.core.centralized.wallet.profilings.utils;

import com.google.common.collect.Range;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    public String OUT_APP_ACTIVATE_PIN_MINUTES;
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
    String SETTING_KEY_VALID_ISSUED_GOVT_IDS;
    String SETTING_KEY_VALID_UTILITY_BILLS;
    String SETTING_KEY_PK_FINCRA;
    String SETTING_BUSINESS_ID_FINCRA;
    String SETTING_FINCRA_CREATE_VIRT_ACCT_URL;
    String SETTING_KEY_FINCRA_SUB_ACCOUNT_CREATE_VIRT_ACCT_URL;
    String SETTING_KEY_FINCRA_SUB_MAIN_ACCOUNT_ID;
    String SETTING_KEY_FINCRA_MAIN_ACCOUNT_ID;
    String Device_Change;
    String SETTING_KEY_VIRTUAL_ACCOUNT_PROVIDER;
    String SETTING_KEY_PRIVD_CLIENT_ID;
    String SETTING_KEY_PRIVD_CLIENT_SEC;
    String SETTING_KEY_PRIVD_X_Auth_Signature;
    String SETTING_KEY_PRIVD_VIRT_BASE_URL;
    String SETTING_DEVICE_LIM_CHECK_PERIOD;
    String SETTING_KEY_CLEARANCES_LIST;
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{6,20}$";
    private static final Pattern PATTERN = Pattern.compile(PASSWORD_PATTERN);
    String SETTING_REF_LINK;
    String SETTING_MER_LINK;
    String SETTING_KEY_PNAMES_LIST;
    @Value("${spring.profiles.active}")
    private String activeProfile;
    @Value("${gen.otp.encrypt.key}")
    private String encryptionKey;

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
        OUT_APP_ACTIVATE_PIN_MINUTES = cache.getApplicationSetting(AppConfigConUtil.OU_APP_ACTIVATE_PIN_MINUTES);
        SETTING_KEY_GET_TIER_1 = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_GET_TIER_1);
        SETTING_KEY_GET_TIER_2 = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_GET_TIER_2);
        SETTING_KEY_GET_TIER_3 = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_GET_TIER_3);
        SETTING_KEY_GET_TIER_4 = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_GET_TIER_4);
        SETTING_KEY_VALID_ISSUED_GOVT_IDS = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_VALID_ISSUED_GOVT_IDS);
        SETTING_KEY_VALID_UTILITY_BILLS = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_VALID_UTILITY_BILLS);
        SETTING_KEY_PK_FINCRA = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_PK_FINCRA);
        SETTING_BUSINESS_ID_FINCRA = cache.getApplicationSetting(AppConfigConUtil.SETTING_BUSINESS_ID_FINCRA);
        SETTING_FINCRA_CREATE_VIRT_ACCT_URL = cache.getApplicationSetting(AppConfigConUtil.SETTING_FINCRA_CREATE_VIRT_ACCT_URL);
        SETTING_KEY_FINCRA_SUB_ACCOUNT_CREATE_VIRT_ACCT_URL = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_FINCRA_SUB_ACCOUNT_CREATE_VIRT_ACCT_URL);
        SETTING_KEY_FINCRA_SUB_MAIN_ACCOUNT_ID = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_FINCRA_SUB_MAIN_ACCOUNT_ID);
        SETTING_KEY_FINCRA_MAIN_ACCOUNT_ID = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_FINCRA_SUB_MAIN_ACCOUNT_ID);
        Device_Change = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_DEVICE_CHANGE);
        SETTING_KEY_VIRTUAL_ACCOUNT_PROVIDER = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_VIRTUAL_ACCOUNT_PROVIDER);
        SETTING_KEY_PRIVD_CLIENT_ID = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_PRIVD_CLIENT_ID);
        SETTING_KEY_PRIVD_X_Auth_Signature = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_PRIVD_X_AUTH_SIG);
        SETTING_KEY_PRIVD_VIRT_BASE_URL = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_PRIVD_VIRT_BASE_URL);
        SETTING_KEY_PRIVD_CLIENT_SEC = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_PRIVD_CLIENT_SEC);
        SETTING_DEVICE_LIM_CHECK_PERIOD = cache.getApplicationSetting(AppConfigConUtil.SETTING_DEVICE_LIM_CHECK_PERIOD);
        SETTING_REF_LINK = cache.getApplicationSetting(AppConfigConUtil.SETTING_REF_LINK);
        SETTING_MER_LINK = cache.getApplicationSetting(AppConfigConUtil.SETTING_MER_LINK);
        SETTING_KEY_CLEARANCES_LIST = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_CLEARANCES_LIST);
        SETTING_KEY_PNAMES_LIST = cache.getApplicationSetting(AppConfigConUtil.SETTING_KEY_PNAMES_LIST);

    }

    public String getSETTING_MER_LINK() {
        return SETTING_MER_LINK;
    }

    public String getSETTING_REF_LINK() {
        return SETTING_REF_LINK;
    }

    public String getSETTING_DEVICE_LIM_CHECK_PERIOD() {
        return SETTING_DEVICE_LIM_CHECK_PERIOD;
    }

    public String getSETTING_KEY_PRIVD_CLIENT_SEC() {
        return SETTING_KEY_PRIVD_CLIENT_SEC;
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

    public String getDevice_Change() {
        return Device_Change;
    }

    public String SETTING_KEY_FINCRA_SUB_ACCOUNT_CREATE_VIRT_ACCT_URL() {
        return SETTING_KEY_FINCRA_SUB_ACCOUNT_CREATE_VIRT_ACCT_URL;
    }

    public String SETTING_KEY_FINCRA_SUB_MAIN_ACCOUNT_ID() {
        return SETTING_KEY_FINCRA_SUB_MAIN_ACCOUNT_ID;
    }

    public String SETTING_KEY_FINCRA_MAIN_ACCOUNT_ID() {
        return SETTING_KEY_FINCRA_MAIN_ACCOUNT_ID;
    }

    public String craeteFincraVirtAcct() {
        return SETTING_FINCRA_CREATE_VIRT_ACCT_URL;
    }

    public String frincaBiz() {
        return SETTING_BUSINESS_ID_FINCRA;
    }

    public String frinaPolicy() {
        return SETTING_KEY_PK_FINCRA;
    }

    private final Pattern alphaPattern = Pattern.compile("^[a-zA-Z]*$");

    public boolean isAlpha(String s) {
        return alphaPattern.matcher(s).find();
    }

    private final Pattern alphaNumericPattern = Pattern.compile("^[a-zA-Z0-9]*$");

    public boolean isAlphaNumeric(String s) {
        return alphaNumericPattern.matcher(s).find();
    }

    public List<String> getUtilitiesBillList() {
        List<String> convertedSETTING_KEY_GOVTID_LIST = Stream.of(SETTING_KEY_VALID_UTILITY_BILLS.split(",", -1))
                .collect(Collectors.toList());
        return convertedSETTING_KEY_GOVTID_LIST;
    }

    public List<String> getGovtIdsList() {
        List<String> convertedSETTING_KEY_GOVTID_LIST = Stream.of(SETTING_KEY_VALID_ISSUED_GOVT_IDS.split(",", -1))
                .collect(Collectors.toList());
        return convertedSETTING_KEY_GOVTID_LIST;
    }

    public List<String> getAllApprovedUtilityBillsList() {
        List<String> convertedSETTING_KEY_GOVTID_LIST = Stream.of(SETTING_KEY_VALID_ISSUED_GOVT_IDS.split(",", -1))
                .collect(Collectors.toList());
        return convertedSETTING_KEY_GOVTID_LIST;
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

    public String getAcyivatePinTimeWindow() {

        return OUT_APP_ACTIVATE_PIN_MINUTES;
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

    private final Pattern Check4Digits = Pattern.compile("\\d{4}");

    public boolean isValid4um(String strNum) {
        if (strNum == null) {
            return false;
        }
        return Check4Digits.matcher(strNum).matches();
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

    public List<String> getClearanceList() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String decSETTING_KEY_CLEARANCES_LIST = decrypt(SETTING_KEY_CLEARANCES_LIST, encryptionKey);
        logger.info(String.format("decSETTING_KEY_CLEARANCES_LIST >>>>>>=>%s", decSETTING_KEY_CLEARANCES_LIST));

        List<String> convertedSETTING_KEY_CLEARANCES_LIST = Stream.of(decSETTING_KEY_CLEARANCES_LIST.split(",", -1))
                .collect(Collectors.toList());

        logger.info(String.format("convertedSETTING_KEY_CLEARANCES_LIST >>>>>>=>%s", convertedSETTING_KEY_CLEARANCES_LIST));

        return convertedSETTING_KEY_CLEARANCES_LIST;
    }

    public List<String> getPNameList() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        //String decSETTING_KEY_CLEARANCES_LIST = decrypt(SETTING_KEY_CLEARANCES_LIST, encryptionKey);
        logger.info(String.format("SETTING_KEY_PNAMES_LIST >>>>>>=>%s", SETTING_KEY_PNAMES_LIST));

        List<String> convertedSETTING_KEY_CLEARANCES_LIST = Stream.of(SETTING_KEY_PNAMES_LIST.split(",", -1))
                .collect(Collectors.toList());

        logger.info(String.format("convertedSETTING_KEY_CLEARANCES_LIST >>>>>>=>%s", convertedSETTING_KEY_CLEARANCES_LIST));

        return convertedSETTING_KEY_CLEARANCES_LIST;
    }

    public boolean getIfClearanceExist(String clearance) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        logger.info(String.format("clearance >>>>>>=>%s", clearance));

        //Before saving
        //Get list of clearances and interate then check against caller-clearance to validate
        List<String> clearances = getClearanceList();
        //  logger.info(String.format("clearances; >>>>>>=>%s", clearances));
        boolean setClearanceToF = false;
        // boolean checkOption = false;
        for (String clearanceType : clearances) {
            //    logger.info(String.format("clearanceType >>>>>>=>%s", clearanceType));
            if (clearance.equals(clearanceType)) {
                setClearanceToF = true;
            }
        }
        return setClearanceToF;

    }

    public boolean lookupProductName(String pName) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        logger.info(String.format("pName >>>>>>=>%s", pName));

        //Before saving
        //Get list of clearances and interate then check against caller-clearance to validate
        List<String> pNames = getPNameList();
        //  logger.info(String.format("clearances; >>>>>>=>%s", clearances));
        boolean setpNameToF = false;
        // boolean checkOption = false;
        for (String pNameType : pNames) {
            //    logger.info(String.format("clearanceType >>>>>>=>%s", clearanceType));
            if (pName.equals(pNameType)) {
                setpNameToF = true;
            }
        }
        return setpNameToF;

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
        //  logger.info(String.format("cutilMeth.getChannelList(); >>>>>>=>%s", channels));
        boolean setChannelToF = false;
        // boolean checkOption = false;
        for (String channelType : channels) {
            //    logger.info(String.format("channelType >>>>>>=>%s", channelType));
            if (channel.equals(channelType)) {
                setChannelToF = true;
            }
        }
        return setChannelToF;

    }

    public boolean getValidIds(String govtId) {
        logger.info(String.format("govtId >>>>>>=>%s", govtId));

        //Before saving
        //Get list of channels and interate then check against caller-channel to validate
        List<String> govtids = getGovtIdsList();
        logger.info(String.format("cutilMeth.getValidIds(); >>>>>>=>%s", govtids));
        boolean setChannelToF = false;
        // boolean checkOption = false;
        for (String govtIdType : govtids) {
            logger.info(String.format("govtIdType >>>>>>=>%s", govtIdType));
            if (govtId.equals(govtIdType)) {
                setChannelToF = true;
            }
        }
        return setChannelToF;

    }

    public boolean getValidUtilits(String utilityBill) {
        logger.info(String.format("utilityBill >>>>>>=>%s", utilityBill));

        //Before saving
        //Get list of channels and interate then check against caller-channel to validate
        List<String> utilIds = getUtilitiesBillList();
        logger.info(String.format("cutilMeth.utilityBill(); >>>>>>=>%s", utilIds));
        boolean setChannelToF = false;
        // boolean checkOption = false;
        for (String govtIdType : utilIds) {
            logger.info(String.format("utilityBill >>>>>>=>%s", govtIdType));
            if (utilityBill.equals(govtIdType)) {
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
    private final Pattern CheckBVNDigits = Pattern.compile("\\d{11}");

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

        String procFrmGlobal = "11" + fromGlobal.substring(0, fromGlobal.length() - 1);
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

    public String genUUID() {

        UUID corrId = UUID.randomUUID();
        return corrId.toString();
    }

    public boolean isPasswordValid(String password) {
        Matcher matcher = PATTERN.matcher(password);
        return matcher.matches();
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

    private static boolean dateWithin10Mins(org.joda.time.LocalDateTime now, String CREATED_DATE, String TIME_IN_MINUTES) {
        return Range.closed(getStartLocalDate(CREATED_DATE), getEndLocalDate(CREATED_DATE, TIME_IN_MINUTES)).contains(now);
    }

    public String generateReferralCode(String serviceName) {
        String servicePrevix = serviceName.substring(0, 2).toUpperCase();
        return servicePrevix + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10).toUpperCase();
    }

    private boolean comparePhoneNumbers(String phoneNumber, String phoneNumber1, String phoneNumber2) {
        logger.info(String.format("phoneNumber=>%s, phoneNumber1=>%s, phoneNumber2=>%s", phoneNumber, phoneNumber1, phoneNumber2));
        boolean phoneNumber1Match = false;
        boolean phoneNumber2Match = false;
        if (StringUtils.hasText(phoneNumber1)) {
            phoneNumber1Match = stripMsisdn(phoneNumber).equals(stripMsisdn(phoneNumber1));
        }
        if (StringUtils.hasText(phoneNumber2)) {
            phoneNumber2Match = stripMsisdn(phoneNumber).equals(stripMsisdn(phoneNumber2));
        }
        return (phoneNumber1Match || phoneNumber2Match);
    }

    private String stripMsisdn(String msisdn) {
        if (msisdn.startsWith("+234")) {
            msisdn = msisdn.substring(4);
        } else if (msisdn.startsWith("234")) {
            msisdn = msisdn.substring(3);
        } else if (msisdn.startsWith("0")) {
            msisdn = msisdn.substring(1);
        }
        return msisdn;
    }

    private boolean compareDatesOfBirth(String frontEndDateString, String bvnDateString) {
        boolean dobsMatch = false;
        String frontendDateFormat = "yyyy-MM-dd"; //1970-11-02
        String bvnDateFormat = "dd-MMM-yyyy";     //02-Nov-1970

        try {
            DateFormat dateFormatter = new SimpleDateFormat(frontendDateFormat);
            Date feDate = dateFormatter.parse(frontEndDateString);

            dateFormatter = new SimpleDateFormat(bvnDateFormat);
            Date bvnDate = dateFormatter.parse(bvnDateString);

            if (StringUtils.isEmpty(frontEndDateString) || StringUtils.isEmpty(bvnDateString)) {
                return dobsMatch;
            } else {
                return (feDate.compareTo(bvnDate) == 0);
            }

        } catch (ParseException e) {
            logger.error("Unable to Parse Dates of Birth: " + frontEndDateString + ", and " + bvnDateString, e);
            return dobsMatch;
        }
    }

}
