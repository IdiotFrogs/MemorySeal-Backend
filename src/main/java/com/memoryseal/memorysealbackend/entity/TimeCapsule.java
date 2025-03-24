package com.memoryseal.memorysealbackend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.memoryseal.memorysealbackend.enums.TimeCapsuleStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    @Column(name = "buried_at", nullable = true)
    private LocalDateTime buriedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    @Column(name = "opend_at", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "time_capsule_status", nullable = false)
    private TimeCapsuleStatus timeCapsuleStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "time_capsule_active_status", nullable = false)
    private Boolean timeCapsuleActiveStatus;

    @Column(name = "user_id", nullable = false)
    private Long userId;

}
