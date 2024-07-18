package com.yap.young.controller;

import com.yap.young.dto.ChildDTO;
import com.yap.young.dto.LoginEmailRequestDTO;
import com.yap.young.dto.LoginRequestDTO;
import com.yap.young.service.auth.AuthService;
import com.yap.young.util.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Create a new child", description = "Creates a new child with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Child created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChildDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/child")
    public ResponseEntity<Object> createChild(@Parameter(description = "Child object to be created. Cannot be null or empty", required = true) @Valid @RequestBody ChildDTO childDTO) {
        return authService.createChild(childDTO);
    }

    @Operation(summary = "Generate token", description = "Generates an authentication token for the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token generated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccessTokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<Object> generateToken(@Parameter(description = "Login request details. Cannot be null or empty", required = true) @Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return authService.generateToken(loginRequestDTO.getUserId(), loginRequestDTO.getPassword());
    }

    @Operation(summary = "Generate token with email and password", description = "Generates an authentication token for the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token generated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccessTokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login_email")
    public ResponseEntity<Object> generateTokenWithEmail(@Parameter(description = "Login request details. Cannot be null or empty", required = true) @Valid @RequestBody LoginEmailRequestDTO request) {
        return authService.generateTokenWithEmail(request.getEmailId(), request.getPassword());
    }

    @Operation(summary = "Update password", description = "Updates the password for the specified user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/update_password")
    public ResponseEntity<Object> updatePassword(@Parameter(description = "User ID of the child. Cannot be null or empty", required = true) @RequestParam String userId, @Parameter(description = "New password. Cannot be null or empty", required = true) @RequestParam Integer password) {
        return authService.updatePassword(userId, password);
    }

    @Operation(summary = "Refresh access token", description = "Use the refresh token to obtain a new access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid refresh token"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/refresh_token")
    public ResponseEntity<Object> refreshToken(@Parameter(description = "Refresh Token Cannot be null or empty", required = true) @RequestParam String refreshToken) {
        return authService.getAccessTokenFromRefreshToken(refreshToken);
    }

    @Operation(summary = "Logout user", description = "Logout the authenticated user and invalidate their session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = AppConstants.USER_LOGGED_OUT_SUCCESS),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User is not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/logout")
    public ResponseEntity<Object> logoutUser(@AuthenticationPrincipal Jwt principal) {
        return authService.logoutUser(principal.getClaimAsString(AppConstants.SUB), principal.getClaimAsString(AppConstants.DEVICE_ID));
    }
}
