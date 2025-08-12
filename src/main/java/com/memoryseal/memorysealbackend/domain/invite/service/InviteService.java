package com.memoryseal.memorysealbackend.domain.invite.service;

import com.memoryseal.memorysealbackend.domain.invite.controller.dto.res.InviteResponseDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.repository.TimeCapsuleJpaRepository;
import com.memoryseal.memorysealbackend.global.redis.util.RandomUtil;
import com.memoryseal.memorysealbackend.global.redis.util.RedisUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class InviteService {
    private final RedisUtil redisUtil;
    private final TimeCapsuleJpaRepository timeCapsuleJpaRepository;

    private static final String INVITE_LINK_PREFIX = "id=%d";

    public InviteResponseDto generateInviteCode(final long id) {
        timeCapsuleJpaRepository.findById(id).orElseThrow(IllegalArgumentException::new);

        final Optional<String> link = redisUtil.getData(INVITE_LINK_PREFIX.formatted(id), String.class);
        if(link.isEmpty()) {
            final String randomCode = RandomUtil.generateRandomCode('0', 'z', 10);
            redisUtil.setDataExpire(INVITE_LINK_PREFIX.formatted(id),randomCode,RedisUtil.toTomorrow());
            return new InviteResponseDto(randomCode);
        }
        return new InviteResponseDto(link.get());
    }
}
