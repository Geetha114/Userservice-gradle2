package com.yap.young.configuration;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.yap.young.exception.s3bucket.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Configuration
public class S3Uploader {

    private final AmazonS3 amazonS3;

    @Value("${awsProperties.bucketName}")
    private String bucketName;

    private static final Logger LOGGER = LoggerFactory.getLogger(S3Uploader.class);

    public S3Uploader(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public void uploadFile(MultipartFile file) {
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, file.getOriginalFilename(), file.getInputStream(), null);
            amazonS3.putObject(putObjectRequest);
            LOGGER.info("File uploaded successfully.");
        } catch (AmazonServiceException e) {
            handleAmazonServiceException(e);
        } catch (SdkClientException e) {
            throw new ClientErrorException("SDK client error: " + e.getMessage());
        } catch (IOException e) {
            throw new IOErrorException("I/O error: " + e.getMessage());
        } catch (SecurityException e) {
            throw new SecurityErrorException("Security error: " + e.getMessage());
        } catch (Exception e) {
            throw new UnexpectedErrorException("Unexpected error: " + e.getMessage());
        }
    }

    private void handleAmazonServiceException(AmazonServiceException e) {
        switch (e.getErrorCode()) {
            case "NoSuchBucket":
                throw new BucketNotFoundException("Bucket does not exist: " + e.getMessage());
            case "AccessDenied":
                throw new AccessDeniedException("Access denied to bucket: " + e.getMessage());
            case "EntityTooLarge":
                throw new FileTooLargeException("File size is too large: " + e.getMessage());
            case "InvalidAccessKeyId":
                throw new InvalidAccessKeyException("Invalid AWS access key ID: " + e.getMessage());
            default:
                throw new S3ServiceException("Amazon service error: " + e.getErrorMessage());
        }
    }

    public String getFileUrl(String originalFilename) {
        return amazonS3.getUrl(bucketName, originalFilename).toString();
    }

    public void deleteObject(String filename) {
        try {
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, filename);

            amazonS3.deleteObject(deleteObjectRequest);
            LOGGER.info("File deleted successfully.");
        } catch (AmazonServiceException e) {
            handleAmazonServiceException(e);
        } catch (SdkClientException e) {
            throw new ClientErrorException("SDK client error: " + e.getMessage());
        } catch (SecurityException e) {
            throw new SecurityErrorException("Security error: " + e.getMessage());
        } catch (Exception e) {
            throw new UnexpectedErrorException("Unexpected error: " + e.getMessage());
        }
    }
}

