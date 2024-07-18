package com.yap.young.util;

import com.yap.young.dto.*;
import com.yap.young.entity.SecretCode;
import com.yap.young.entity.UserDevice;
import com.yap.young.entity.UserProfile;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.Map;

public class EntityToDtoMapper {

    private EntityToDtoMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static SecretCode mapSecretCodeDtoToSecretCode(SecretCodeDTO secretCodeDTO, String childId) {
        SecretCode secretCode = new SecretCode();
        secretCode.setCode(CommonUtils.generateRandomNumber(4));
        secretCode.setChildId(childId);
        secretCode.setParentId(secretCodeDTO.getParentId());
        return secretCode;
    }

    public static ChildResponseDTO mapUserRepresentationToChildResponseDTO(UserRepresentation userRepresentation) {
        ChildResponseDTO childResponseDTO = new ChildResponseDTO();
        Map<String, List<String>> attributes = userRepresentation.getAttributes();
        childResponseDTO.setUserId(userRepresentation.getId());
        childResponseDTO.setEmail(CommonUtils.maskEmail(userRepresentation.getEmail()));
        childResponseDTO.setFirstName(userRepresentation.getFirstName());

        String mobileNumber = getAttributeIfPresent(attributes, AppConstants.MOBILE);
        childResponseDTO.setMobile(CommonUtils.maskMobileNumber(mobileNumber));
        return childResponseDTO;
    }

    public static String getAttributeIfPresent(Map<String, List<String>> attributes, String attribute) {
        return attributes.get(attribute) != null && !attributes.get(attribute).isEmpty() ? attributes.get(attribute).get(0) : null;
    }

    public static AccessTokenResponseDTO mapAccessTokenResponseToDTO(String userId, AccessTokenResponse accessTokenResponse) {
        return new AccessTokenResponseDTO(accessTokenResponse.getToken(), accessTokenResponse.getExpiresIn(), accessTokenResponse.getRefreshToken(), accessTokenResponse.getRefreshExpiresIn(), accessTokenResponse.getTokenType(), userId);
    }

    public static UserDevice mapDeviceBindRequestDTO(String userId, String deviceId, String osVersion, String location) {
        return UserDevice.builder()
                .userId(userId)
                .deviceId(deviceId)
                .osVersion(osVersion)
                .location(location)
                .isActive(true)
                .build();
    }


    public static UserProfileResponseDTO mapUserProfileToUserProfileDTO(UserProfile userProfile, UserRepresentation user, String mobile) {
        return UserProfileResponseDTO.builder()
                .userId(userProfile.getChildId())
                .profilePictureUrl(userProfile.getProfilePictureUrl())
                .yapTag(userProfile.getYapTag())
                .email(user.getEmail())
                .mobile(mobile)
                .build();
    }

}
