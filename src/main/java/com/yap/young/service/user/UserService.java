package com.yap.young.service.user;

import com.yap.young.dto.DeviceBindRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface UserService {

    ResponseEntity<Object> getYapTag(String userId, Optional<String> tagName);

    ResponseEntity<Object> saveUserProfileDetails(MultipartFile file, String cardColor, String cardDisplayName, String yapTag, String sub);

    ResponseEntity<Object> getDataByScanDocument(String parentId, String documentType, MultipartFile frontImage, MultipartFile backImage);

    ResponseEntity<Object> bindDeviceToUser(String userId, DeviceBindRequestDTO deviceBindRequestDTO);

    ResponseEntity<Object> checkDeviceBinding(String userId, String deviceId);

    ResponseEntity<Object> unboundDeviceFromUser(String claimAsString, String claimAsString1);

    ResponseEntity<Object> updateUserImage(String userId, MultipartFile file);

    ResponseEntity<Object> getUserDetails(String userId);

    ResponseEntity<Object> checkPasswordAgainstUser(String userId, String password);
}
