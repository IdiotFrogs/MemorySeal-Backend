package com.memoryseal.memorysealbackend.domain.file.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_attached_file")
public class AttachedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_size", length = 100, nullable = false)
    private String fileSize;

    @Column(name = "file_url", length = 100, nullable = false)
    private String fileUrl;

    @Column(name = "file_type", nullable = false)
    private FileType fileType;

    @Column(name = "contend_id")
    private Long contentId;
}
