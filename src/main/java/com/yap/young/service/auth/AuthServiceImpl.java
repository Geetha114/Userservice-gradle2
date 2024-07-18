package com.yap.young.service.auth;

import com.yap.young.configuration.KeycloakConfig;
import com.yap.young.dto.AccessTokenResponseDTO;
import com.yap.young.dto.ChildDTO;
import com.yap.young.entity.Country;
import com.yap.young.entity.UserDevice;
import com.yap.young.exception.ResourceNotFoundException;
import com.yap.young.repository.CountryRepository;
import com.yap.young.repository.UserDeviceRepository;
import com.yap.young.util.CommonUtils;
import com.yap.young.util.EntityToDtoMapper;
import com.yap.young.util.ResponseHandler;
import com.yap.young.util.AppConstants;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.Response;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final KeycloakConfig keycloakConfig;

    private final CountryRepository countryRepository;

    private final UserDeviceRepository userDeviceRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    public AuthServiceImpl(KeycloakConfig keycloakConfig, CountryRepository countryRepository, UserDeviceRepository userDeviceRepository) {
        this.keycloakConfig = keycloakConfig;
        this.countryRepository = countryRepository;
        this.userDeviceRepository = userDeviceRepository;
    }

    @Override
    public ResponseEntity<Object> createChild(ChildDTO childDTO) {
        LOGGER.info("Child creation requested by parent ID: {}", childDTO.getParentId());
        try {
            String username = CommonUtils.getUsername(childDTO.getFullName()) + CommonUtils.generateRandomNumber(4);
            Response keycloakResponse = createChildInKeycloak(username, childDTO);

            if (Objects.equals(keycloakResponse.getStatus(), 201)) {
                String userId = keycloakResponse.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

                LOGGER.info(AppConstants.USER_SUCCESS_LOG, username, userId);

                RoleRepresentation roleRepresentation = keycloakConfig.getRoleRepresentation(AppConstants.ROLE_BENEFICIARY);
                keycloakConfig.getUserResource().get(userId).roles().realmLevel().add(Collections.singletonList(roleRepresentation));

                return ResponseHandler.generateResponse(AppConstants.USER_CREATION_SUCCESS_MSG, HttpStatus.CREATED, userId);
            } else {
                return ResponseHandler.generateResponse(AppConstants.USER_CREATION_FAILED_MSG, HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private Response createChildInKeycloak(String username, ChildDTO childDTO) {
        UserRepresentation user = getUserRepresentation(username, childDTO);
        return keycloakConfig.getUserResource().create(user);
    }

    private UserRepresentation getUserRepresentation(String username, ChildDTO childDTO) {
        try {
            UserRepresentation user = new UserRepresentation();
            user.setUsername(username);
            user.setEmail(childDTO.getEmail());
            user.setFirstName(CommonUtils.getFirstNameFromString(childDTO.getFullName()));
            user.setLastName(CommonUtils.getLastNameFromString(childDTO.getFullName()));
            setUserAttributes(user, childDTO);
            return user;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private void setUserAttributes(UserRepresentation user, ChildDTO childDTO) {
        Country country = getCountryById(childDTO.getCountryId());
        user.singleAttribute(AppConstants.FULL_NAME, childDTO.getFullName());
        user.singleAttribute(AppConstants.GENDER, childDTO.getGender());
        user.singleAttribute(AppConstants.DOB, String.valueOf(childDTO.getDob()));
        user.singleAttribute(AppConstants.COUNTRY, country.getName());
        user.singleAttribute(AppConstants.PARENT_ID, childDTO.getParentId());
        Optional.ofNullable(childDTO.getMobile()).ifPresent(mobile -> user.singleAttribute(AppConstants.MOBILE, childDTO.getMobile()));
    }

    @Override
    public ResponseEntity<Object> generateToken(String userId, String password) {
        LOGGER.info("Generate token requested by user ID: {}", userId);
        UserRepresentation userRepresentation = keycloakConfig.getUserById(userId);
        try {
            return ResponseEntity.ok(getAccessToken(userRepresentation.getId(), userRepresentation.getUsername(), password));
        } catch (Exception e) {
            return ResponseHandler.generateResponse(AppConstants.ERROR_USER_INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        }
    }

    private AccessTokenResponseDTO getAccessToken(String userId, String username, String password) {
        AccessTokenResponse accessTokenResponse = keycloakConfig.getAccessToken(username, String.valueOf(password)).tokenManager().getAccessToken();
        return EntityToDtoMapper.mapAccessTokenResponseToDTO(userId, accessTokenResponse);
    }

    @Override
    public ResponseEntity<Object> generateTokenWithEmail(String emailId, String password) {
        UserRepresentation userRepresentation = keycloakConfig.getUsernameByEmailId(emailId);
        try {
            return ResponseEntity.ok(getAccessToken(userRepresentation.getId(), userRepresentation.getUsername(), password));
        } catch (Exception e) {
            return ResponseHandler.generateResponse(AppConstants.ERROR_USER_INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<Object> updatePassword(String userId, Integer password) {
        LOGGER.info("Update user password requested by user ID: {}", userId);
        UserRepresentation userRepresentation = keycloakConfig.getUserById(userId);
        try {
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setValue(String.valueOf(password));
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setTemporary(false);

            String username = keycloakConfig.updateUserCredential(userRepresentation, credential);

            LOGGER.info(AppConstants.USER_PASSWORD_UPDATE_SUCCESS_LOG, userId);

            return ResponseHandler.generateResponse(AppConstants.PASSWORD_UPDATE_SUCCESS, HttpStatus.OK, getAccessToken(userId, username, String.valueOf(password)));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private Country getCountryById(Integer countryId) {
        return countryRepository.findById(countryId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.ERROR_COUNTRY_NOT_FOUND + countryId));
    }

    @Override
    public ResponseEntity<Object> getAccessTokenFromRefreshToken(String refreshToken) {
        String userId = keycloakConfig.getUserIdFromToken(refreshToken);
        LOGGER.info("Get access token from refresh token by user ID: {}", userId);
        AccessTokenResponse accessTokenResponse = keycloakConfig.getAccessTokenFromRefreshToken(refreshToken).getBody();
        try {
            if (accessTokenResponse != null) {
                LOGGER.info("Generated access token for the user ID: {}", userId);
                return ResponseEntity.ok(EntityToDtoMapper.mapAccessTokenResponseToDTO(userId, accessTokenResponse));
            }
            return ResponseHandler.generateResponse(AppConstants.ERROR_TOKEN_IS_NOT_VALID, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    @Override
    public ResponseEntity<Object> logoutUser(String userId, String deviceId) {
        LOGGER.info("Logging out for the user ID: {}", userId);
        UserRepresentation userRepresentation = keycloakConfig.getUserById(userId);
        UserDevice userDevice = getUserDevice(userId, deviceId);
        try {
            keycloakConfig.updateUserCustomAttribute(userRepresentation, AppConstants.DEVICE_ID, null);
            updateUserDevice(userDevice);
            keycloakConfig.logoutUser(userRepresentation.getId());
            LOGGER.info("Successfully logged out for the user ID: {}", userId);
            return ResponseHandler.generateResponse(AppConstants.USER_LOGGED_OUT_SUCCESS, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private UserDevice getUserDevice(String userId, String deviceId) {
        return userDeviceRepository.findByUserIdAndDeviceIdAndIsActive(userId, deviceId, true)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.ERROR_DEVICE_NOT_BOUNDED));
    }

    private void updateUserDevice(UserDevice userDevice) {
        userDevice.setIsActive(false);
        userDeviceRepository.save(userDevice);
        LOGGER.info("Successfully Unbounded the device from the user Id: {}", userDevice.getUserId());
    }
}