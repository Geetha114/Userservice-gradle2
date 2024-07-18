package com.yap.young.exception;

import lombok.Getter;

@Getter
public class MissingParameterException extends RuntimeException {

    private final String parameterName;

    public MissingParameterException(String parameterName) {
        super(parameterName + " cannot be null or empty");
        this.parameterName = parameterName;
    }

}
