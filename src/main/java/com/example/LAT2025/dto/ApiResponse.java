package com.example.LAT2025.dto;

public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    
    public ApiResponse() {
    }
    
    public ApiResponse(T data, String message) {
        this.success = true;
        this.data = data;
        this.message = message;
    }
    
    public ApiResponse(String message, boolean success) {
        this.success = success;
        this.message = message;
    }
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(data, message);
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(message, false);
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
} 