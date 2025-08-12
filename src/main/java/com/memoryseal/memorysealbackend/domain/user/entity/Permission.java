package com.memoryseal.memorysealbackend.domain.user.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_permission")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image", nullable = false)
    private Boolean image;

    @Column(name = "mic", nullable = false)
    private Boolean mic;

    @Column(name = "push_notification", nullable = false)
    private Boolean pushNotification;

    @Column(name = "user_id", nullable = false)
    private Long userId;
}
