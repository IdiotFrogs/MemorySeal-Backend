package com.memoryseal.memorysealbackend.domain.seasonal_push.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_seasonal_push")
public class SeasonalPush {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "season", nullable = false)
    private Season season;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "time_capsule_id", nullable = false)
    private Long timeCapsuleId;

    @Column(name = "send_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "confirmed_at", nullable = true)
    private LocalDateTime confirmedAt;

    public void confirm() {
        this.confirmedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}
