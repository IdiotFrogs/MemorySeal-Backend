package com.memoryseal.memorysealbackend.repository;

import com.memoryseal.memorysealbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User,Long> {
    Optional<User> findByNickname(String nickname);
}
