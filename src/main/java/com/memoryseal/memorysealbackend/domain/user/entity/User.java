package com.memoryseal.memorysealbackend.domain.user.entity;

import com.memoryseal.memorysealbackend.domain.auth.entity.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_user")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname",length = 16, nullable = false)
    private String nickname;

    @Column(name = "profile_url", length = 100, nullable = true)
    private String profileUrl;

    @Column(name = "email", length = 50, nullable = false)
    private String email;

    @Column(name = "user_active_status", nullable = false)
    private Boolean userActiveStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;
}