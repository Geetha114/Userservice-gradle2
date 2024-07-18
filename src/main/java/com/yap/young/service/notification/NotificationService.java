package com.yap.young.service.notification;

import com.yap.young.dto.SecretCodeDTO;
import com.yap.young.dto.VerificationRequestDTO;
import org.springframework.http.ResponseEntity;

public interface NotificationService {

    ResponseEntity<Object> createSecretCode(SecretCodeDTO secretCodeDTO);

    ResponseEntity<Object> validateCode(Integer code);

    ResponseEntity<Object> sendVerificationCodeEmail(String userId);

    ResponseEntity<Object> verifyEmailCode(VerificationRequestDTO verificationRequest);

    ResponseEntity<Object> sendVerificationCodeMobile(String userId);

    ResponseEntity<Object> verifyMobileCode(VerificationRequestDTO verificationRequest);
}
