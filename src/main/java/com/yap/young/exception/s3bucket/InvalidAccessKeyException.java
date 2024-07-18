package com.yap.young.exception.s3bucket;

public class InvalidAccessKeyException extends S3ServiceException {
    public InvalidAccessKeyException(String message) {
        super(message);
    }
}
