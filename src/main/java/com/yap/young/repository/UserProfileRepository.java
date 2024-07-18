package com.yap.young.repository;

import com.yap.young.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    boolean existsByYapTag(String tagName);

    Optional<UserProfile> findByChildId(String userId);
}
