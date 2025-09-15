package com.memoryseal.memorysealbackend.domain.user.entity;

import com.memoryseal.memorysealbackend.domain.auth.entity.Role;
import com.memoryseal.memorysealbackend.domain.file.entity.AttachedFile;
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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_image_id", unique = true)
    private AttachedFile profileImage;

    @Column(name = "email", length = 50, nullable = false)
    private String email;

    @Column(name = "user_active_status", nullable = false)
    private Boolean userActiveStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;
}