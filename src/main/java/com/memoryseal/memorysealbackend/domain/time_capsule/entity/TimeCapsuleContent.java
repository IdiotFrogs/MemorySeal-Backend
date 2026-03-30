package com.memoryseal.memorysealbackend.domain.time_capsule.entity;

import com.memoryseal.memorysealbackend.domain.file.entity.AttachedFile;
import com.memoryseal.memorysealbackend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_time_capsule_content")
public class TimeCapsuleContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", columnDefinition = "TEXT", nullable = true)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_capsule_id", nullable = false)
    private TimeCapsule timeCapsule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "timeCapsuleContent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttachedFile> attachedFiles = new ArrayList<>();

    public void addAttachedFile(AttachedFile file) {
        this.attachedFiles.add(file);
        file.setTimeCapsuleContent(this);
    }
}
