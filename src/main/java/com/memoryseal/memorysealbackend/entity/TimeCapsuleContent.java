package com.memoryseal.memorysealbackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_time_capsule_content")
public class TimeCapsuleContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", length = 255, nullable = true)
    private String content;

    @Column(name = "time_capsule_id", nullable = false)
    private Long timeCapsuleId;

}
