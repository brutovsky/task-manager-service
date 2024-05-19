package com.nakytniak.entity;

import java.util.Arrays;

public enum FileType {
    JSON("json"), CSV("csv"), OTHER("");

    public String getExt() {
        return ext;
    }

    private final String ext;

    FileType(final String ext) {
        this.ext = ext;
    }

    public static FileType getType(final String filename) {
        final String fileExt = filename.substring(filename.lastIndexOf('.'));
        return Arrays.stream(FileType.values()).filter(e -> e.getExt().endsWith(fileExt)).findFirst().orElse(OTHER);
    }
}
