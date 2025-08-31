package com.memoryseal.memorysealbackend.domain.contributor.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_contributor_request")
@NoArgsConstructor
@Getter
public class ContributorRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //요청 상태
    @Column(name = "status", nullable = false)
    private ContributorRequestStatus status;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "time_capsule_id", nullable = false)
    private Long timeCapsuleId;

    @Builder
    public ContributorRequest(Long userId, Long timeCapsuleId) {
        this.userId = userId;
        this.timeCapsuleId = timeCapsuleId;
        this.status = ContributorRequestStatus.PENDING;
    }

    public void updateStatus(ContributorRequestStatus status) {
        this.status = status;
    }
}
