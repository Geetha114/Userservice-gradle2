package com.yap.young.exception.s3bucket;

public class AccessDeniedException extends S3ServiceException {

    public AccessDeniedException(String message) {
        super(message);
    }
}
