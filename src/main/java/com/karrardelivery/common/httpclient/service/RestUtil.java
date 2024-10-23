package com.karrardelivery.common.httpclient.service;


import org.springframework.http.HttpStatusCode;

/**
 * Utility class for common HTTP related operations.
 */
public final class RestUtil {

    // Private constructor to prevent instantiation.
    private RestUtil() {
        throw new AssertionError("Cannot instantiate RestUtil class.");
    }

    /**
     * Checks if the provided HttpStatus represents an error.
     *
     * @param status the HttpStatusCode to check.
     * @return true if the status represents an error, false otherwise.
     */
    public static boolean isError(HttpStatusCode status) {
        return status.isError();
    }
}