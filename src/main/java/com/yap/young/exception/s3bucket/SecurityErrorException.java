package com.yap.young.exception.s3bucket;

public class SecurityErrorException extends S3ServiceException {
    public SecurityErrorException(String message) {
        super(message);
    }
}
