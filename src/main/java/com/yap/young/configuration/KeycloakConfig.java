package com.yap.young.configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yap.young.exception.ResourceNotFoundException;
import com.yap.young.util.AppConstants;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Configuration
public class KeycloakConfig {

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    RestTemplate restTemplate = new RestTemplate();

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }

    public Keycloak getAccessToken(String username, String password) {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(realm)
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(username)
                .password(password)
                .build();
    }

    public UsersResource getUserResource() {
        try {
            return keycloak().realm(realm).users();
        } catch (NotFoundException e) {
            throw new ResourceNotFoundException(AppConstants.ERROR_REALM_NOT_FOUND);
        }
    }

    public UserRepresentation getUserById(String userId) {
        try {
            return getUserResource().get(userId).toRepresentation();
        } catch (NotFoundException e) {
            throw new ResourceNotFoundException(AppConstants.ERROR_USER_NOT_FOUND + userId);
        }
    }

    public String updateUserCredential(UserRepresentation userRepresentation, CredentialRepresentation credential) {
        UserResource userResource = getUserResource().get(userRepresentation.getId());
        userResource.resetPassword(credential);

        userRepresentation = userResource.toRepresentation();
        userRepresentation.setEnabled(true);
        userResource.update(userRepresentation);
        return userRepresentation.getUsername();
    }

    public RoleRepresentation getRoleRepresentation(String roleBeneficiary) {
        return keycloak().realm(realm).roles().get(roleBeneficiary).toRepresentation();
    }

    public String getAttributeFromUser(UserRepresentation user, String attribute) {
        if (user != null) {
            return user.getAttributes().get(attribute) != null ? user.getAttributes().get(attribute).get(0) : null;
        }
        return null;
    }

    public void updateUserAttribute(UserRepresentation userRepresentation) {
        userRepresentation.setEmailVerified(true);
        UserResource userResource = getUserResource().get(userRepresentation.getId());
        userResource.update(userRepresentation);
    }

    public ResponseEntity<AccessTokenResponse> getAccessTokenFromRefreshToken(String refreshToken) {
        try {
            String tokenUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
            formParams.add(AppConstants.CLIENT_ID, clientId);
            formParams.add(AppConstants.CLIENT_SECRET, clientSecret);
            formParams.add(AppConstants.GRANT_TYPE, AppConstants.REFRESH_TOKEN);
            formParams.add(AppConstants.REFRESH_TOKEN, refreshToken);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formParams, headers);
            return restTemplate.postForEntity(tokenUrl, request, AccessTokenResponse.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new IllegalArgumentException(ex);
            } else {
                throw new InternalServerErrorException();
            }
        }
    }

    public String getUserIdFromToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getSubject();
        } catch (JWTDecodeException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public UserRepresentation getUsernameByEmailId(String emailId) {
        return getUserByEmailId(emailId);
    }

    private UserRepresentation getUserByEmailId(String emailId) {
        List<UserRepresentation> users = getUserResource().searchByEmail(emailId, true);
        if (users.isEmpty()) {
            throw new ResourceNotFoundException(AppConstants.ERROR_USER_NOT_FOUND + emailId);
        }
        return users.get(0);
    }

    public void updateUserCustomAttribute(UserRepresentation user, String customAttribute, String value) {
        Map<String, List<String>> existingAttributes = user.getAttributes();
        existingAttributes.put(customAttribute, value != null ? List.of(value) : null);

        user.setAttributes(existingAttributes);
        getUserResource().get(user.getId()).update(user);
    }

    public void logoutUser(String userId) {
        UserResource userResource = getUserResource().get(userId);
        userResource.logout();
    }
}