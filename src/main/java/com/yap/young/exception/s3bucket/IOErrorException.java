package com.yap.young.exception.s3bucket;

public class IOErrorException extends S3ServiceException {
    public IOErrorException(String message) {
        super(message);
    }
}
