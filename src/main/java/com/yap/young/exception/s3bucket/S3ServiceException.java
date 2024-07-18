package com.yap.young.exception.s3bucket;

public class S3ServiceException extends RuntimeException {

    public S3ServiceException(String message) {
        super(message);
    }
}

