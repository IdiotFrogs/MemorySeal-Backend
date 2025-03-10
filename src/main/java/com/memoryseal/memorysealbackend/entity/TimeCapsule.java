package com.memoryseal.memorysealbackend.entity;

import com.memoryseal.memorysealbackend.enums.TimeCapsuleStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_time_capsule")
public class TimeCapsule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    //묻히는 날짜


    //열리는 날짜

    @Column(name = "time_capsule_status", nullable = false)
    private TimeCapsuleStatus timeCapsuleStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "time_capsule_active_status", nullable = false)
    private Boolean timeCapsuleActiveStatus;

    @Column(name = "user_id", nullable = false)
    private String userId;

}
