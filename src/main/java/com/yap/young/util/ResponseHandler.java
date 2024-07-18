package com.yap.young.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {

    private ResponseHandler() {
        throw new IllegalStateException("Utility class");
    }

    public static ResponseEntity<Object> generateResponse(String message, HttpStatus status, Object responseObj,
                                                          int count) {
        Map<String, Object> map = new HashMap<>();
        map.put(AppConstants.MESSAGE, message);
        map.put(AppConstants.STATUS_CODE, status.value());
        map.put(AppConstants.DATA, responseObj);
        map.put(AppConstants.TOTAL_COUNT, count);

        return new ResponseEntity<>(map, status);
    }

    public static ResponseEntity<Object> generateResponse(String message, HttpStatus status, Object responseObj) {
        Map<String, Object> map = new HashMap<>();
        map.put(AppConstants.MESSAGE, message);
        map.put(AppConstants.STATUS_CODE, status.value());
        map.put(AppConstants.DATA, responseObj);

        return new ResponseEntity<>(map, status);
    }

    public static ResponseEntity<Object> generateResponse(String message, HttpStatus status) {
        Map<String, Object> map = new HashMap<>();
        map.put(AppConstants.MESSAGE, message);
        map.put(AppConstants.STATUS_CODE, status.value());

        return new ResponseEntity<>(map, status);
    }
}
