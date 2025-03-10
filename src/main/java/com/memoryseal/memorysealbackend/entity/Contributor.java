package com.memoryseal.memorysealbackend.entity;

import com.memoryseal.memorysealbackend.enums.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_contributor")
public class Contributor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "bury", nullable = true)
    private Boolean bury;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "time_capsule_id", nullable = false)
    private Long timeCapsuleId;
}
