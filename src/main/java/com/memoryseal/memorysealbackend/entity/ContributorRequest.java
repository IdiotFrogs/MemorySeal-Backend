package com.memoryseal.memorysealbackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_contributor_request")
public class ContributorRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //요청 상태

    //공동작업자 승인 유저

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "time_capsule_id", nullable = false)
    private Long timeCapsuleId;
}
