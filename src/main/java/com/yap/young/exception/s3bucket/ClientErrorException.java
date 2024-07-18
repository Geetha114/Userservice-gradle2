package com.yap.young.exception.s3bucket;

public class ClientErrorException extends S3ServiceException {
    public ClientErrorException(String message) {
        super(message);
    }
}
