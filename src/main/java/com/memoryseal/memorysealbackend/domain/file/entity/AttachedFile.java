package com.memoryseal.memorysealbackend.domain.file.entity;

import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleContent;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_attached_file")
public class AttachedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "file_url", length = 255, nullable = false)
    private String fileUrl;

    @Column(name = "file_type", nullable = false)
    private FileType fileType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contend_id")
    private TimeCapsuleContent timeCapsuleContent;

    @Column(name = "is_main")
    private Boolean isMain;

    @OneToOne(mappedBy = "mainImage", fetch = FetchType.LAZY)
    private TimeCapsule timeCapsuleImage;
}
