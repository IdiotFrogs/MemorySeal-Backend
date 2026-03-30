package com.memoryseal.memorysealbackend.domain.user.repository;

import com.memoryseal.memorysealbackend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User,Long> {
    Optional<User> findByNickname(String nickname);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndProvider(String email, String provider);
    Optional<User> findByProviderAndProviderIdAndUserActiveStatus(String provider, String providerId, Boolean userActiveStatus);
    Boolean existsByProviderAndProviderIdAndUserActiveStatus(String provider, String providerId, Boolean userActiveStatus);
    boolean existsByEmail(String email);
}
