package com.memoryseal.memorysealbackend.service;

import com.memoryseal.memorysealbackend.dto.ContributorResponseDto;
import com.memoryseal.memorysealbackend.domain.contributor.entity.Contributor;
import com.memoryseal.memorysealbackend.repository.ContributorJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContributorService {
    private final ContributorJpaRepository contributorJpaRepository;

    public List<ContributorResponseDto> getDetail(Long capsuleId) {
        List<Contributor> contributor = contributorJpaRepository.findByTimeCapsuleId(capsuleId);

        return contributor.stream()
                .map(ContributorResponseDto::toDto)
                .collect(Collectors.toList());
    }
}
