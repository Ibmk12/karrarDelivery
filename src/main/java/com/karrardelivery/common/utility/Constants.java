package com.karrardelivery.common.utility;

public class Constants {


    public static final Integer SUCCESS_TRANSACTION_CODE = 1;
    public static final String SUCCESS_TRANSACTION_MESSAGE = "Successful";

    public static final String DB_ERROR_MESSAGE = "Database error";
    public static final Integer DB_ERROR_CODE = 601;

    public static final String SESSION_INITIATION_ERROR_MESSAGE = "Failed to initiate new session";
    public static final Integer SESSION_INITIATION_ERROR_CODE = 602;

    public static final String UPDATE_SESSION_ERROR_MESSAGE = "Failed to update session";
    public static final Integer UPDATE_SESSION_ERROR_CODE = 603;

    public static final String EXECUTE_PAYMENT_ERROR_MESSAGE = "Failed to execute payment";
    public static final Integer EXECUTE_PAYMENT_ERROR_CODE = 604;

    public static final String PROCESS_SESSION_ERROR_MESSAGE = "Failed to process the initiated session";
    public static final Integer PROCESS_SESSION_ERROR_CODE = 605;


    public static final Integer CONNECT_TIMEOUT_CODE = 500;
    public static final String CONNECT_TIMEOUT_STATUS = "Failed";

    public static final Integer INPUT_OUTPUT_PROCESSING_ERROR_CODE = 500;
    public static final String INPUT_OUTPUT_PROCESSING_ERROR_CODE_STATUS = "Failed";
    public static final String  INPUT_OUTPUT_PROCESSING_ERROR_CODE_MESSAGE = "IO ERROR WHILE PROCESSING TRANSACTION";

    public static final Integer OPEN_CIRCUIT_CODE = 902;
    public static final String OPEN_CIRCUIT_STATUS = "Failed";
    public static final String OPEN_CIRCUIT_MESSAGE = "OPEN CIRCUIT DUE TO PREVIOUS FAILURES";

    public static final Integer UNAUTHORIZED_CODE = 400;
    public static final String UNAUTHORIZED_MESSAGE = "Unauthorized GW request";

    public static final Integer VALIDATION_INVALID_REQUEST_TYPE_ACTION_DECLINE_CODE = 401;
    public static final String VALIDATION_INVALID_REQUEST_TYPE_ACTION_DECLINE_MESSAGE = "INTERNAL PROCESSING ERROR IN GW";

    public static final Integer VALIDATION_INVALID_AMOUNT_CODE = 405;
    public static final String VALIDATION_INVALID_AMOUNT_MESSAGE = "Invalid Amount";

    public static final Integer TRANSACTION_NOT_FOUND_CODE = 36;
    public static final String TRANSACTION_NOT_FOUND_CODE_MESSAGE = "No record found";

    public static final Integer TRANSACTION_FAILED_CODE = 33;
    public static final String TRANSACTION_FAILED_MESSAGE = "Failed";

    public static final Integer INVALID_REQUEST_TYPE_DECLINE_CODE = 401;
    public static final String INVALID_REQUEST_TYPE_DECLINE_MESSAGE = "INTERNAL PROCESSING ERROR IN GW";

    public static final String DEFAULT_CURRENCY = "AED";

    public static final String DEFAULT_CURRENCY_CODE = "40";

    public static final String FAILURE_STATUS = "Failed";

    public static final String SUCCESS_CODE = "200";

    public static final String Not_FOUND_CODE = "404";

    public class ORDER_STATUS {

        public static final String DELIVERED = "Delivered";
        public static final String CANCELED = "Canceled";
        public static final String UNDER_DELIVERY = "Under Delivery";
        public static final String EXCHANGED = "Exchanged";
    }
    }
