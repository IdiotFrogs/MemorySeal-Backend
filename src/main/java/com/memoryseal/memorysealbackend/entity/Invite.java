package com.memoryseal.memorysealbackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_invite")
public class Invite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //초대 링크 공개키


    @Column(name = "modification_status", nullable = false)
    private Boolean modificationStatus;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "time_capsule_id", nullable = false)
    private Long timeCapsuleId;

}
