package com.nakytniak.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TaskEntityType {
    STUDENTS_INFO("students_info");
    private final String collectionName;
}
