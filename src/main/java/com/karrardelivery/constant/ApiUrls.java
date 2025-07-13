package com.karrardelivery.constant;

public class ApiUrls {

    public final static String BASE_URL = "/api/v1/";
    public final static String ORDERS = BASE_URL + "orders";
    public final static String TRADER = BASE_URL + "traders";
    public final static String REPORT = "report";
    public final static String REPORT_STATUS = REPORT + "/status";
    public final static String EXPORT = BASE_URL + "export";
    public final static String EXPORT_ORDERS = EXPORT + "/orders";
    public final static String DAILY_REPORT = "/daily-report";
    public final static String LOGO_PATH = "/static/logo.png";
    public final static String ORDER_META_DATA = "/metadata";
    public final static String USERS = BASE_URL + "users";
    public final static String ENABLE_USERS =  "/{id}/enable";
    public final static String DISABLE_USERS = "/{id}/disable";
    public final static String CHANGE_PASSWORD = "/change-password";
}
