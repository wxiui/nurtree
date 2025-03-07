package com.csstj.nurtree.common.consts;

public class AppConst {

    public static boolean ISADMIN = false;

    /**
     * 主机地址
     * public static String HOST = "http://114.116.110.9/prod-api";
     * public static String HOST = "http://172.16.4.192:8081";
     */
    public static String HOST = "http://172.16.4.192:8081";
    /**
     * 登录
     */
    public static final String LOGIN = HOST + "/login";

    public static final String REGISTER =  HOST + "/nurtreeReg";

    public static final String GET_RES_LIST = HOST + "/nurtree/resources/applist";

    public static final String GET_ADMIN_RES_LIST = HOST + "/nurtree/resources/appAdminlist";

    public static final String GET_QUES_LIST = HOST + "/nurtree/resources/getQuestions";

    public static final String SAVE_RES_INFO = HOST + "/nurtree/resources/saveAppUserResource";

    public static final String GET_CONFIG_INFO = HOST + "/system/config/configKey/";
}
