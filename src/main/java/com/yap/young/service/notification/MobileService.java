package com.yap.young.service.notification;

import com.yap.young.dto.DestinationDTO;
import com.yap.young.dto.MessagesWrapperDTO;
import com.yap.young.dto.SMSMessageDTO;
import com.yap.young.dto.enums.ExceptionType;
import com.yap.young.dto.enums.NotificationStatus;
import com.yap.young.entity.NotificationLog;
import com.yap.young.util.AppConstants;
import com.yap.young.util.CommonUtils;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MobileService {

    @Value("${infobip.api.url}")
    private String apiUrl;

    @Value("${infobip.api.key}")
    private String apiKey;

    @Value("${infobip.api.from}")
    private String apiFrom;

    private final NotificationLogService notificationLogService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MobileService.class);

    public Response sendSms(String userId, String mobileNumber, int verificationCode, MessagesWrapperDTO payload) {
        LOGGER.info("Send SMS for the user id: {}", userId);
        NotificationLog notificationLog = new NotificationLog();
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse(AppConstants.APPLICATION_JSON);
            RequestBody body = RequestBody.create(CommonUtils.getJSONFromPayload(payload), mediaType);
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .method(HttpMethod.POST, body)
                    .addHeader(AppConstants.AUTHORIZATION, apiKey)
                    .addHeader(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON)
                    .addHeader(AppConstants.ACCEPT, AppConstants.APPLICATION_JSON)
                    .build();
            notificationLog = notificationLogService.createLogForNotification(userId, mobileNumber, String.valueOf(payload), AppConstants.MOBILE, NotificationStatus.SENT, verificationCode);
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                notificationLogService.updateLogForNotification(notificationLog, NotificationStatus.PROCESSED, null);
            } else {
                String responseBody = response.body() != null ? response.body().string() : "No response body";
                notificationLogService.updateLogForNotification(notificationLog, NotificationStatus.FAILED, response.code() + responseBody);
            }
            LOGGER.info("SMS triggered for the user id: {}", userId);
            return response;
        } catch (Exception e) {
            handleException(e, notificationLog);
            throw new InternalServerErrorException();
        }
    }

    public Response sendVerificationCodeMobile(String userId, String firstName, String mobileNumber, int verificationCode) {
        try {
            SMSMessageDTO smsMessageDTO = new SMSMessageDTO();

            DestinationDTO destinationDTO = new DestinationDTO();
            destinationDTO.setTo(mobileNumber);

            smsMessageDTO.setDestinations(List.of(destinationDTO));
            smsMessageDTO.setFrom(apiFrom);
            smsMessageDTO.setText("Hi " + firstName + " Please use the following verification code to complete your registration: " + verificationCode);
            MessagesWrapperDTO messagesWrapperDTO = new MessagesWrapperDTO();
            messagesWrapperDTO.setMessages(List.of(smsMessageDTO));
            return sendSms(userId, mobileNumber, verificationCode, messagesWrapperDTO);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    public void handleException(Exception e, NotificationLog notificationLog) {
        String errorMessage;
        ExceptionType exceptionType;

        if (e instanceof IOException) {
            errorMessage = "IOException occurred: " + e.getMessage();
            exceptionType = ExceptionType.IO_EXCEPTION;
        } else if (e instanceof HttpException) {
            errorMessage = "HttpException occurred: " + e.getMessage();
            exceptionType = ExceptionType.HTTP_EXCEPTION;
        } else {
            errorMessage = "Unexpected exception occurred: " + e.getMessage();
            exceptionType = ExceptionType.GENERAL_EXCEPTION;
        }

        LOGGER.error(errorMessage, e);
        notificationLogService.updateLogForNotification(notificationLog, NotificationStatus.FAILED, errorMessage);

        switch (exceptionType) {
            case IO_EXCEPTION:
                throw new InternalServerErrorException("IOException occurred while sending notification", e);
            case HTTP_EXCEPTION:
                throw new InternalServerErrorException("HttpException occurred while sending notification", e);
            case GENERAL_EXCEPTION:
            default:
                throw new InternalServerErrorException("Unexpected exception occurred while sending notification", e);
        }
    }
}