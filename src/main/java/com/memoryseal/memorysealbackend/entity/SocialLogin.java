package com.memoryseal.memorysealbackend.entity;

import com.memoryseal.memorysealbackend.enums.SocialType;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_social_login")
public class SocialLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "social_type", nullable = false)
    private SocialType socialType;

    @Column(name = "user_id", nullable = false)
    private Long userId;
}
