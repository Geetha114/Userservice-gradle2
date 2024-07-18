package com.yap.young.service.auth;

import com.yap.young.dto.ChildDTO;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<Object> createChild(ChildDTO childDTO);

    ResponseEntity<Object> generateToken(String userId, String password);

    ResponseEntity<Object> updatePassword(String userId, Integer password);

    ResponseEntity<Object> getAccessTokenFromRefreshToken(String refreshToken);

    ResponseEntity<Object> logoutUser(String userId, String deviceId);

    ResponseEntity<Object> generateTokenWithEmail(String emailId, String password);
}