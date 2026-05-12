package com.memoryseal.memorysealbackend.domain.time_capsule.service;

import com.memoryseal.memorysealbackend.domain.contributor.entity.Contributor;
import com.memoryseal.memorysealbackend.domain.contributor.repository.ContributorJpaRepository;
import com.memoryseal.memorysealbackend.domain.file.entity.AttachedFile;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.req.TimeCapsuleContentRequest;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.TimeCapsuleContentListResDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.TimeCapsuleContentResDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.UserContentDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleContent;
import com.memoryseal.memorysealbackend.domain.time_capsule.repository.ContentJpaRepository;
import com.memoryseal.memorysealbackend.domain.time_capsule.repository.TimeCapsuleJpaRepository;
import com.memoryseal.memorysealbackend.domain.user.entity.User;
import com.memoryseal.memorysealbackend.domain.user.repository.UserJpaRepository;
import com.memoryseal.memorysealbackend.global.aws.service.S3Service;
import com.memoryseal.memorysealbackend.global.error.ErrorCode;
import com.memoryseal.memorysealbackend.global.error.Exception.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimeCapsuleContentService {
    private final TimeCapsuleJpaRepository timeCapsuleJpaRepository;
    private final ContentJpaRepository contentJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final S3Service s3Service;
    private final ContributorJpaRepository contributorJpaRepository;


    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()) {
            throw new AuthException(ErrorCode.NEED_LOGIN);
        }
        Object principal = authentication.getPrincipal();
        Long currentUserId;
        if(principal instanceof User) {
            currentUserId = ((User) principal).getId();
        }else if(principal instanceof String) {
            currentUserId = Long.valueOf((String) principal);
        }else {
            log.error("예상치 못한 Principal 타입: {}", principal.getClass().getName());
            throw new AuthException(ErrorCode.NEED_LOGIN);
        }
        return currentUserId;
    }


    @Transactional
    public TimeCapsuleContentResDto createContent(Long timeCapsuleId, TimeCapsuleContentRequest request, List<MultipartFile> files) {
        TimeCapsule timeCapsule = timeCapsuleJpaRepository.findById(timeCapsuleId)
                .orElseThrow(() -> new AuthException(ErrorCode.TIMECAPSULE_NOT_FOUND));
        Long currentUserId = getCurrentUserId();
        User user = userJpaRepository.findById(currentUserId)
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));


        if(!contributorJpaRepository.existsByTimeCapsuleIdAndUserId(timeCapsuleId, currentUserId)) {
            throw new AuthException(ErrorCode.ACCESS_DENIED);
        }

        boolean hasContent = (request != null) && (request.getContent() != null) && (!request.getContent().trim().isEmpty());
        boolean hasFiles = files != null && !files.isEmpty();

        if(!hasContent && !hasFiles) {
            throw new AuthException(ErrorCode.EMPTY_CONTENT);
        }

        TimeCapsuleContent content = TimeCapsuleContent.builder()
                .content(hasContent ? request.getContent() : null)
                .timeCapsule(timeCapsule)
                .user(user)
                .build();

        if(files != null && !files.isEmpty()) {
            List<AttachedFile> attachedFiles = files.parallelStream()
                    .map(file -> {
                        try {
                            return s3Service.uploadContentFile(file);
                        } catch (IOException e) {
                            throw new AuthException(ErrorCode.FILE_UPLOAD_ERROR);
                        }
                    })
                    .toList();
            attachedFiles.forEach(content::addAttachedFile);
        }
        contentJpaRepository.save(content);
        return TimeCapsuleContentResDto.toDto(content);
    }

    public TimeCapsuleContentResDto updateContent(Long contentId, String newContent) {
        TimeCapsuleContent content = contentJpaRepository.findById(contentId).orElseThrow(
                () -> new AuthException(ErrorCode.CONTENT_NOT_FOUND)
        );
        content.setContent(newContent);
        contentJpaRepository.save(content);
        return TimeCapsuleContentResDto.toDto(content);
    }

    public TimeCapsuleContentListResDto getMyContent(Long timeCapsuleId) {
        Long currentUserId = getCurrentUserId();
        Contributor contributor = contributorJpaRepository.findByUserIdAndTimeCapsuleId(currentUserId, timeCapsuleId)
                .orElseThrow(() -> new AuthException(ErrorCode.ACCESS_DENIED));

        List<TimeCapsuleContent> contents = contentJpaRepository.findByTimeCapsuleId(timeCapsuleId);
        List<Long> userIds = contents.stream()
                .map(c -> c.getUser().getId())
                .distinct()
                .toList();

        Map<Long, User> userMap = userJpaRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        Map<Long, List<TimeCapsuleContent>> groupByUser = contents.stream()
                .collect(Collectors.groupingBy(c -> c.getUser().getId()));

        List<UserContentDto> userContentDtos = groupByUser.entrySet().stream()
                .map(entry -> {
                    User user = userMap.get(entry.getKey());
                    return UserContentDto.builder()
                            .userId(entry.getKey())
                            .nickname(user.getNickname())
                            .capsuleContents(entry.getValue().stream()
                                    .map(TimeCapsuleContentResDto::toDto)
                                    .toList())
                            .build();
                })
                .toList();

        return TimeCapsuleContentListResDto.builder()
                .myRole(contributor.getContributorRole())
                .userContents(userContentDtos)
                .build();
    }

    @Transactional
    public void deleteContent(Long contentId) {
        Long currentUserId = getCurrentUserId();

        TimeCapsuleContent content = contentJpaRepository.findById(contentId)
                .orElseThrow(() -> new AuthException(ErrorCode.CONTENT_NOT_FOUND));

        if(!content.getUser().getId().equals(currentUserId)) {
            throw new AuthException(ErrorCode.ACCESS_DENIED);
        }

        contentJpaRepository.delete(content);
    }
}
