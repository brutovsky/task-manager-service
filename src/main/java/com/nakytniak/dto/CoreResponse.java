package com.nakytniak.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreResponse<E> {
    private E entity;
    private String errorMessage;

    public static <E> CoreResponse<E> of(final E entity) {
        return CoreResponse.<E>builder()
                .entity(entity)
                .build();
    }
}