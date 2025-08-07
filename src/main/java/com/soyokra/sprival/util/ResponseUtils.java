package com.soyokra.sprival.util;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseUtils<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private static final Integer DEFAULT_SUCCESS_CODE = 0;
    
    private static final String DEFAULT_SUCCESS_MESSAGE = "success";

    private int code;

    private String message;

    private T data;

    public ResponseUtils() {

    }

    public ResponseUtils(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseUtils<T> success() {
        return info(DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MESSAGE, null);
    }

    public static <T> ResponseUtils<T> success(T data) {
        return info(DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MESSAGE, data);
    }

    public static <T> ResponseUtils<T> error(int code, String message) {
        return info(code, message, null);
    }

    public static <T> ResponseUtils<T> info(int code, String message, T data) {
        return new ResponseUtils<>(code, message, data);
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public T getData() {
        return this.data;
    }
}
