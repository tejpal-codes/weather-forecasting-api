package com.example.weatherforecasting.exception;

public abstract class BaseAppException extends RuntimeException{

    public BaseAppException(String message) {
        super(message);
    }

    public BaseAppException(String message, Throwable cause) {
        super(message, cause);
    }

}
