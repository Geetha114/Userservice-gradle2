package com.yap.young.repository;

import com.yap.young.entity.SecretCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SecretCodeRepository extends JpaRepository<SecretCode, Long> {

    Optional<SecretCode> findByCode(Integer code);

    List<SecretCode> findByChildIdAndIsUsedAndIsExpired(String childId, boolean isUsed, boolean isExpired);
}
