package com.yap.young.exception.s3bucket;

public class UnexpectedErrorException extends S3ServiceException {
    public UnexpectedErrorException(String message) {
        super(message);
    }
}
