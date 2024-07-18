package com.yap.young.service.notification;

import com.yap.young.configuration.KeycloakConfig;
import com.yap.young.dto.ChildResponseDTO;
import com.yap.young.dto.SecretCodeDTO;
import com.yap.young.dto.VerificationRequestDTO;
import com.yap.young.entity.NotificationType;
import com.yap.young.entity.SecretCode;
import com.yap.young.entity.VerificationCode;
import com.yap.young.exception.ResourceNotFoundException;
import com.yap.young.repository.NotificationTypeRepository;
import com.yap.young.repository.SecretCodeRepository;
import com.yap.young.repository.NotificationRepository;
import com.yap.young.util.AppConstants;
import com.yap.young.util.CommonUtils;
import com.yap.young.util.EntityToDtoMapper;
import com.yap.young.util.ResponseHandler;
import jakarta.ws.rs.InternalServerErrorException;
import okhttp3.Response;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final SecretCodeRepository secretCodeRepository;

    private final KeycloakConfig keycloakConfig;

    private final EmailService emailService;

    private final NotificationRepository notificationRepository;

    private final MobileService mobileService;

    private final NotificationTypeRepository notificationTypeRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);

    public NotificationServiceImpl(SecretCodeRepository secretCodeRepository, KeycloakConfig keycloakConfig, EmailService emailService, NotificationRepository notificationRepository, MobileService mobileService, NotificationTypeRepository notificationTypeRepository) {
        this.secretCodeRepository = secretCodeRepository;
        this.keycloakConfig = keycloakConfig;
        this.emailService = emailService;
        this.notificationRepository = notificationRepository;
        this.mobileService = mobileService;
        this.notificationTypeRepository = notificationTypeRepository;
    }

    @Override
    public ResponseEntity<Object> createSecretCode(SecretCodeDTO secretCodeDTO) {
        LOGGER.info("createSecretCode for child ID: {}", secretCodeDTO.getChildId());
        keycloakConfig.getUserById(secretCodeDTO.getChildId());
        try {
            List<SecretCode> unusedCodes = secretCodeRepository.findByChildIdAndIsUsedAndIsExpired(secretCodeDTO.getChildId(), false, false);

            for (SecretCode unusedCode : unusedCodes) {
                boolean isExpired = CommonUtils.isCurrentTimeGreaterThanCreatedPlusSeconds(unusedCode.getCreatedAt(), LocalDateTime.now(), 900);
                if (isExpired) {
                    unusedCode.setIsExpired(true);
                } else {
                    unusedCode.setIsUsed(true);
                }
                unusedCode.setUpdatedAt(LocalDateTime.now());
            }

            if (!unusedCodes.isEmpty())
                secretCodeRepository.saveAll(unusedCodes);

            SecretCode secretCode = EntityToDtoMapper.mapSecretCodeDtoToSecretCode(secretCodeDTO, secretCodeDTO.getChildId());
            secretCodeRepository.save(secretCode);
            LOGGER.info(AppConstants.SECRET_CODE_GENERATION_LOG, secretCodeDTO.getChildId());
            return ResponseHandler.generateResponse(AppConstants.SECRET_CODE_GENERATION_SUCCESS_MSG, HttpStatus.CREATED, secretCode.getCode());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    @Override
    public ResponseEntity<Object> validateCode(Integer code) {
        LOGGER.info("validateCode for child");
        SecretCode secretCode = getSecretCodeByCode(code);
        try {
            if (secretCode.getIsUsed() != null && Boolean.TRUE.equals(secretCode.getIsUsed())) {
                return ResponseHandler.generateResponse(AppConstants.ERROR_SECRET_CODE_ALREADY_USED, HttpStatus.CONFLICT, code);
            }
            if (secretCode.getIsExpired() != null && Boolean.TRUE.equals(secretCode.getIsExpired())) {
                return ResponseHandler.generateResponse(AppConstants.ERROR_SECRET_CODE_EXPIRED, HttpStatus.GONE, code);
            }

            boolean isExpiredByTime = CommonUtils.isCurrentTimeGreaterThanCreatedPlusSeconds(secretCode.getCreatedAt(), LocalDateTime.now(), 900);
            if (isExpiredByTime) {
                updateSecretCodeStatus(secretCode, false, true);
                return ResponseHandler.generateResponse(AppConstants.ERROR_SECRET_CODE_EXPIRED, HttpStatus.GONE, code);
            }
            updateSecretCodeStatus(secretCode, true, false);
            LOGGER.info(AppConstants.SECRET_CODE_VALIDATION_LOG, secretCode.getChildId());
            return ResponseHandler.generateResponse(AppConstants.SUCCESS_SECRET_CODE_VERIFIED, HttpStatus.OK, getKeycloakUserInfo(secretCode.getChildId()));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private void updateSecretCodeStatus(SecretCode secretCode, boolean isUsed, boolean isExpired) {
        secretCode.setIsUsed(isUsed);
        secretCode.setIsExpired(isExpired);
        secretCode.setUpdatedAt(LocalDateTime.now());
        secretCodeRepository.save(secretCode);
    }

    private ChildResponseDTO getKeycloakUserInfo(String userId) {
        UserRepresentation userRepresentation = keycloakConfig.getUserById(userId);
        return EntityToDtoMapper.mapUserRepresentationToChildResponseDTO(userRepresentation);
    }

    private SecretCode getSecretCodeByCode(Integer code) {
        return secretCodeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.ERROR_SECRET_CODE_NOT_FOUND + code));
    }

    @Override
    public ResponseEntity<Object> sendVerificationCodeEmail(String userId) {
        LOGGER.info("sendVerificationCodeEmail for child Id: {}", userId);
        UserRepresentation userRepresentation = keycloakConfig.getUserById(userId);
        try {
            if (userRepresentation.getEmail() != null) {
                int verificationCode = CommonUtils.generateRandomNumber(6);
                emailService.sendVerificationCodeEmail(userId, userRepresentation.getFirstName(), userRepresentation.getEmail(), verificationCode);

                createVerificationCodeEntry(verificationCode, userId, 1);
                LOGGER.info(AppConstants.EMAIL_SUCCESS_MSG + " {}", userId);
                return ResponseHandler.generateResponse(AppConstants.EMAIL_SUCCESS_MSG, HttpStatus.OK);
            } else
                return ResponseHandler.generateResponse(AppConstants.EMAIL_NOT_FOUND, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private void createVerificationCodeEntry(int code, String userId, Integer notificationTypeId) {
        LOGGER.info("createVerificationCodeEntry for child Id: {}", userId);
        List<VerificationCode> unusedCodes = notificationRepository.findByChildIdAndIsUsedAndIsExpiredAndNotificationType(userId, false, false, getNotificationTypeById(notificationTypeId));
        for (VerificationCode unusedCode : unusedCodes) {
            boolean isExpired = CommonUtils.isCurrentTimeGreaterThanCreatedPlusSeconds(unusedCode.getCreatedAt(), LocalDateTime.now(), 90);
            if (isExpired) {
                unusedCode.setIsExpired(true);
            } else {
                unusedCode.setIsUsed(true);
            }
            unusedCode.setUpdatedAt(LocalDateTime.now());
        }

        if (!unusedCodes.isEmpty())
            notificationRepository.saveAll(unusedCodes);

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setChildId(userId);
        verificationCode.setNotificationType(getNotificationTypeById(notificationTypeId));
        verificationCode.setCode(code);
        notificationRepository.save(verificationCode);
    }

    private NotificationType getNotificationTypeById(Integer notificationTypeId) {
        return notificationTypeRepository.findById(notificationTypeId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.ERROR_NOTIFICATION_TYPE_NOT_FOUND));
    }

    @Override
    public ResponseEntity<Object> verifyEmailCode(VerificationRequestDTO verificationRequest) {
        LOGGER.info("verifyEmailCode for child Id: {}", verificationRequest.getUserId());
        UserRepresentation userRepresentation = keycloakConfig.getUserById(verificationRequest.getUserId());
        VerificationCode verificationCode = checkVerificationCode(verificationRequest.getUserId(), verificationRequest.getVerificationCode(), getNotificationTypeById(1));
        try {
            if (verificationCode.getIsUsed() != null && Boolean.TRUE.equals(verificationCode.getIsUsed())) {
                return ResponseHandler.generateResponse(AppConstants.ERROR_SECRET_CODE_ALREADY_USED, HttpStatus.CONFLICT, verificationRequest.getVerificationCode());
            }
            if (verificationCode.getIsExpired() != null && Boolean.TRUE.equals(verificationCode.getIsExpired())) {
                return ResponseHandler.generateResponse(AppConstants.ERROR_SECRET_CODE_EXPIRED, HttpStatus.GONE, verificationRequest.getVerificationCode());
            }

            boolean isExpiredByTime = CommonUtils.isCurrentTimeGreaterThanCreatedPlusSeconds(verificationCode.getCreatedAt(), LocalDateTime.now(), 180);
            if (isExpiredByTime) {
                updateVerificationCodeStatus(verificationCode, false, true);
                return ResponseHandler.generateResponse(AppConstants.ERROR_SECRET_CODE_EXPIRED, HttpStatus.GONE, verificationCode.getCode());
            }
            updateVerificationCodeStatus(verificationCode, true, false);
            if (Boolean.FALSE.equals(verificationRequest.getIsRegistered()))
                keycloakConfig.updateUserAttribute(userRepresentation);
            LOGGER.info("Email verification code verified for the user. User ID: {}", verificationRequest.getUserId());
            return ResponseHandler.generateResponse(AppConstants.SUCCESS_SECRET_CODE_VERIFIED, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private void updateVerificationCodeStatus(VerificationCode verificationCode, boolean isUsed, boolean isExpired) {
        verificationCode.setIsUsed(isUsed);
        verificationCode.setIsExpired(isExpired);
        verificationCode.setUpdatedAt(LocalDateTime.now());
        notificationRepository.save(verificationCode);
    }

    private VerificationCode checkVerificationCode(String userId, Integer verificationCode, NotificationType notificationType) {
        return notificationRepository.findByChildIdAndCodeAndNotificationType(userId, verificationCode, notificationType)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.ERROR_SECRET_CODE_NOT_FOUND + verificationCode));
    }

    @Override
    public ResponseEntity<Object> sendVerificationCodeMobile(String userId) {
        LOGGER.info("sendVerificationCodeMobile for child Id: {}", userId);
        UserRepresentation userRepresentation = keycloakConfig.getUserById(userId);
        try {
            String mobileNumber = EntityToDtoMapper.getAttributeIfPresent(userRepresentation.getAttributes(), AppConstants.MOBILE);
            if (mobileNumber != null) {
                int verificationCode = CommonUtils.generateRandomNumber(6);
                Response response = mobileService.sendVerificationCodeMobile(userId, userRepresentation.getFirstName(), mobileNumber, verificationCode);
                if (response.isSuccessful()) {
                    createVerificationCodeEntry(verificationCode, userId, 2);
                    LOGGER.info(AppConstants.MOBILE_SUCCESS_MSG + " {}", userId);
                    return ResponseHandler.generateResponse(AppConstants.MOBILE_SUCCESS_MSG, HttpStatus.OK);
                }
                return ResponseHandler.generateResponse(response.message(), HttpStatus.valueOf(response.code()));
            } else
                return ResponseHandler.generateResponse(AppConstants.MOBILE_NUMBER_NOT_FOUND, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    @Override
    public ResponseEntity<Object> verifyMobileCode(VerificationRequestDTO verificationRequest) {
        LOGGER.info("verifyMobileCode for child Id: {}", verificationRequest.getUserId());
        keycloakConfig.getUserById(verificationRequest.getUserId());
        VerificationCode verificationCode = checkVerificationCode(verificationRequest.getUserId(), verificationRequest.getVerificationCode(), getNotificationTypeById(2));
        try {
            if (verificationCode.getIsUsed() != null && Boolean.TRUE.equals(verificationCode.getIsUsed())) {
                return ResponseHandler.generateResponse(AppConstants.ERROR_SECRET_CODE_ALREADY_USED, HttpStatus.CONFLICT, verificationRequest.getVerificationCode());
            }
            if (verificationCode.getIsExpired() != null && Boolean.TRUE.equals(verificationCode.getIsExpired())) {
                return ResponseHandler.generateResponse(AppConstants.ERROR_SECRET_CODE_EXPIRED, HttpStatus.GONE, verificationRequest.getVerificationCode());
            }

            boolean isExpiredByTime = CommonUtils.isCurrentTimeGreaterThanCreatedPlusSeconds(verificationCode.getCreatedAt(), LocalDateTime.now(), 180);
            if (isExpiredByTime) {
                updateVerificationCodeStatus(verificationCode, false, true);
                return ResponseHandler.generateResponse(AppConstants.ERROR_SECRET_CODE_EXPIRED, HttpStatus.GONE, verificationCode.getCode());
            }
            updateVerificationCodeStatus(verificationCode, true, false);
            LOGGER.info("Verification code verified for the user. User ID: {}", verificationRequest.getUserId());
            return ResponseHandler.generateResponse(AppConstants.SUCCESS_SECRET_CODE_VERIFIED, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }
}
