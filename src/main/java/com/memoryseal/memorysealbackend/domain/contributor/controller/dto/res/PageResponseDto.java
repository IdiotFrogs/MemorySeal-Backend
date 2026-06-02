package com.memoryseal.memorysealbackend.domain.contributor.controller.dto.res;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
public class PageResponseDto<T> {
    private List<T> content;
    private int totalPages;
    private Long totalElements;
    private int number;
    private boolean last;

    public PageResponseDto(Page<T> page) {
        this.content = page.getContent();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.number = page.getNumber();
        this.last = page.isLast();
    }
}
