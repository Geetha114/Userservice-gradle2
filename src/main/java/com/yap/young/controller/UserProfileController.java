package com.yap.young.controller;

import com.yap.young.dto.DeviceBindRequestDTO;
import com.yap.young.service.user.UserService;
import com.yap.young.util.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/user_profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;

    @Operation(summary = "Save User Profile Details", description = "Save user profile details including optional profile picture upload.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile details saved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request: Missing required parameters or invalid data format"),
            @ApiResponse(responseCode = "401", description = "Unauthorized: User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Forbidden: Access to resource denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Object> saveUserProfileDetails(@Parameter(description = "Upload a profile picture") @RequestParam(value = "file", required = false) MultipartFile file, @RequestParam(value = "cardColor", required = false) String cardColor,
                                                         @RequestParam(value = "cardName", required = false) String cardDisplayName, @RequestParam(value = "tagName", required = false) String yapTag, @AuthenticationPrincipal Jwt principal) {
        return userService.saveUserProfileDetails(file, cardColor, cardDisplayName, yapTag, principal.getClaimAsString(AppConstants.SUB));
    }

    @Operation(summary = "Get Yap Tag by Username", description = "Retrieve Yap tags associated with a username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved Yap tags"),
            @ApiResponse(responseCode = "400", description = "Bad request: Missing required parameters or invalid data format"),
            @ApiResponse(responseCode = "401", description = "Unauthorized: User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Forbidden: Access to resource denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/yap_tag")
    public ResponseEntity<Object> getYapTag(@AuthenticationPrincipal Jwt principal, @RequestParam(name = "tagName", required = false) Optional<String> yapTag) {
        return userService.getYapTag(principal.getClaimAsString(AppConstants.SUB), yapTag);
    }

    @Operation(summary = "Check if the device is already bound")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device binding status retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/device")
    public ResponseEntity<Object> checkDeviceBinding(@Parameter(description = "User id. cannot be empty or null") @RequestParam String userId, @Parameter(description = "Device id. cannot be empty or null") @RequestParam String deviceId) {
        return userService.checkDeviceBinding(userId, deviceId);
    }

    @Operation(summary = "Bind a device to a user's account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = AppConstants.USER_DEVICE_CREATED_SUCCESS),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/device")
    public ResponseEntity<Object> bindDevice(@AuthenticationPrincipal Jwt principal, @Valid @RequestBody DeviceBindRequestDTO deviceBindRequestDTO) {
        return userService.bindDeviceToUser(principal.getClaimAsString(AppConstants.SUB), deviceBindRequestDTO);
    }

    @Operation(summary = "Unbound a device from a user's account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = AppConstants.USER_DEVICE_UNBOUNDED_SUCCESS),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/device")
    public ResponseEntity<Object> unboundDevice(@AuthenticationPrincipal Jwt principal) {
        return userService.unboundDeviceFromUser(principal.getClaimAsString(AppConstants.SUB), principal.getClaimAsString(AppConstants.DEVICE_ID));
    }

    @Operation(summary = "Update profile picture for the user", description = "Update the user image associated with a userId.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = AppConstants.USER_IMAGE_UPDATED_SUCCESS),
            @ApiResponse(responseCode = "400", description = "Bad request: Missing required parameters or invalid data format"),
            @ApiResponse(responseCode = "401", description = "Unauthorized: User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Forbidden: Access to resource denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(value = "/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Object> updateUserImage(@AuthenticationPrincipal Jwt principal, @Parameter(description = "Upload a profile picture for a user", required = true) @RequestParam(value = "file") MultipartFile file) {
        return userService.updateUserImage(principal.getClaimAsString(AppConstants.SUB), file);
    }

    @Operation(summary = "Get user profile details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = AppConstants.USER_PROFILE_RETRIEVED_SUCCESS),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<Object> getUserDetails(@AuthenticationPrincipal Jwt principal) {
        return userService.getUserDetails(principal.getClaimAsString(AppConstants.SUB));
    }

    @Operation(summary = "Check if the given password matches the user's password in Keycloak")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = AppConstants.PASSWORD_VERIFIED_SUCCESS),
            @ApiResponse(responseCode = "401", description = "Password is incorrect"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/check_password")
    public ResponseEntity<Object> checkPassword(@AuthenticationPrincipal Jwt principal, @RequestParam String password) {
        return userService.checkPasswordAgainstUser(principal.getClaimAsString(AppConstants.SUB), password);
    }
}