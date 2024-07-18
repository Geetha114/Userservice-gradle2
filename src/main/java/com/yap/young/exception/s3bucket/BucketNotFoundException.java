package com.yap.young.exception.s3bucket;

public class BucketNotFoundException extends S3ServiceException {
    public BucketNotFoundException(String message) {
        super(message);
    }
}
