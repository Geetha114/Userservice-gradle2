package com.yap.young.exception.s3bucket;

public class FileTooLargeException extends S3ServiceException {
    public FileTooLargeException(String message) {
        super(message);
    }
}
