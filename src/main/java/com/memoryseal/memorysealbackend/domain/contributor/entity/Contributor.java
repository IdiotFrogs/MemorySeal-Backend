package com.memoryseal.memorysealbackend.entity;

import com.memoryseal.memorysealbackend.enums.ContributorRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_contributor")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Contributor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contributor_role", nullable = false)
    private ContributorRole contributorRole;

    @Column(name = "bury", nullable = true)
    private Boolean bury;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "time_capsule_id", nullable = false)
    private Long timeCapsuleId;
}
