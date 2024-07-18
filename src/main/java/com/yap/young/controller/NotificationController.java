package com.yap.young.controller;

import com.yap.young.dto.SecretCodeDTO;
import com.yap.young.dto.VerificationRequestDTO;
import com.yap.young.exception.annotation.WithRateLimitProtection;
import com.yap.young.service.notification.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Create a secret code", description = "Creates a new secret code for the child")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Secret code created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SecretCodeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    @PostMapping("/secret_code")
    @WithRateLimitProtection
    public ResponseEntity<Object> createSecretCode(@Parameter(description = "Secret code object to be created. Cannot be null or empty", required = true) @Valid @RequestBody SecretCodeDTO secretCodeDTO) {
        return notificationService.createSecretCode(secretCodeDTO);
    }

    @Operation(summary = "Validate secret code", description = "Validates the provided secret code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Secret code validated",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Secret code not found")
    })
    @GetMapping("/secret_code/validate")
    public ResponseEntity<Object> validateSecretCode(@Parameter(description = "Secret code to be validated. Cannot be null or empty", required = true) @RequestParam Integer code) {
        return notificationService.validateCode(code);
    }

    @Operation(summary = "Send verification code email", description = "Sends a verification code to the user's email address.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification email sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/send_verification_email")
    public ResponseEntity<Object> sendVerificationCodeEmail(@Parameter(description = "User ID of the child. Cannot be null or empty", required = true) @RequestParam String userId) {
        return notificationService.sendVerificationCodeEmail(userId);
    }

    @Operation(summary = "Verify email code", description = "Verifies the code sent to the user's email address.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification successful"),
            @ApiResponse(responseCode = "400", description = "Invalid verification code or user ID"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/verify_email")
    public ResponseEntity<Object> verifyEmailCode(@Valid @RequestBody VerificationRequestDTO verificationRequest) {
        return notificationService.verifyEmailCode(verificationRequest);
    }

    @Operation(summary = "Send verification code mobile", description = "Sends a verification code to the user's mobile number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification mobile sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/send_verification_mobile")
    public ResponseEntity<Object> sendVerificationCodeMobile(@RequestParam String userId) {
        return notificationService.sendVerificationCodeMobile(userId);
    }

    @Operation(summary = "Verify mobile code", description = "Verifies the code sent to the user's mobile number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification successful"),
            @ApiResponse(responseCode = "400", description = "Invalid verification code or user ID"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/verify_mobile")
    public ResponseEntity<Object> verifyMobileCode(@Valid @RequestBody VerificationRequestDTO verificationRequest) {
        return notificationService.verifyMobileCode(verificationRequest);
    }
}