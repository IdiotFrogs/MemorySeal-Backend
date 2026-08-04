package com.memoryseal.memorysealbackend.domain.time_capsule.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_time_capsule_watering")
public class TimeCapsuleWatering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "time_capsule_id", nullable = false)
    private Long timeCapsuleId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "watered_date", nullable = false)
    private LocalDate wateredDate;

}
