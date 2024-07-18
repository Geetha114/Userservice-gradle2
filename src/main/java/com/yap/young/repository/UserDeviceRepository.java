package com.yap.young.repository;

import com.yap.young.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {

    Optional<UserDevice> findByUserIdAndDeviceIdAndIsActive(String userId, String deviceId, Boolean isActive);

    List<UserDevice> findByUserIdAndIsActive(String userId, Boolean isActive);

    Optional<UserDevice> findByUserIdAndDeviceId(String userId, String deviceId);
}
