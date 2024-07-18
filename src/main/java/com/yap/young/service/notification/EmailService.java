package com.yap.young.service.notification;

import com.yap.young.dto.enums.NotificationStatus;
import com.yap.young.entity.NotificationLog;
import com.yap.young.util.AppConstants;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.ws.rs.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    private final NotificationLogService notificationLogService;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    public void sendHtmlMessage(String to, String subject, String template, Context context, int verificationCode, String userId) {
        NotificationLog notificationLog = new NotificationLog();
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            String fromAddress = "noreply.ksa@yap.com";
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);

            String htmlBody = templateEngine.process(template, context);
            helper.setText(htmlBody, true);
            notificationLog = notificationLogService.createLogForNotification(userId, to, htmlBody, AppConstants.EMAIL, NotificationStatus.SENT, verificationCode);

            mailSender.send(mimeMessage);
            notificationLogService.updateLogForNotification(notificationLog, NotificationStatus.PROCESSED, null);
        } catch (MessagingException | MailException e) {
            handleException(e, notificationLog, "Failed to send email");
        } catch (Exception e) {
            handleException(e, notificationLog, "Unexpected error occurred");
        }
    }

    public void sendVerificationCodeEmail(String userId, String firstName, String to, int verificationCode) {
        try {
            Context context = new Context();
            context.setVariable(AppConstants.NAME, firstName);
            context.setVariable(AppConstants.VERIFICATION_CODE_OBJ, verificationCode);
            sendHtmlMessage(to, AppConstants.VERIFICATION_CODE, AppConstants.VERIFICATION_CODE_EMAIL_TEMPLATE, context, verificationCode, userId);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private void handleException(Exception e, NotificationLog notificationLog, String errorMessage) {
        LOGGER.error(e.getMessage(), e);
        String detailedMessage = e instanceof MessagingException || e instanceof MailException ? e.getMessage() : "Unexpected exception occurred: " + e.getMessage();
        notificationLogService.updateLogForNotification(notificationLog, NotificationStatus.FAILED, detailedMessage);
        throw new InternalServerErrorException(errorMessage, e);
    }

    @Async
    public void sendDeviceRegisterConfirmationToUser(String userId, String name, String to, String osVersion) {
        try {
            Context context = new Context();
            context.setVariable(AppConstants.NAME, name);
            context.setVariable(AppConstants.OS_VERSION, osVersion);
            sendHtmlMessage(to, AppConstants.SECURITY_ALERT, AppConstants.DEVICE_SIGN_IN_CONFIRMATION_EMAIL_TEMPLATE, context, 0, userId);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }
}
