package com.serhiishcherbakov.notebooks.domain.common;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PageResult<T> {
    private List<T> content;
    private int page;
    private int size;
    private int total;
}
