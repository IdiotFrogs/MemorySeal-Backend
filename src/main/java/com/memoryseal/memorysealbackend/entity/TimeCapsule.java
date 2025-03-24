package com.memoryseal.memorysealbackend.entity;

import com.memoryseal.memorysealbackend.enums.TimeCapsuleStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "tb_time_capsule")
public class TimeCapsule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "buried_at", nullable = true)
    private LocalDateTime buriedAt;


    @Column(name = "opend_at", nullable = false)
    private LocalDateTime openedAt;

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
