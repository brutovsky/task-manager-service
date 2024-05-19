package com.nakytniak.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "file")
public class FileEntry extends CommonEntity {
    private Integer schoolId;
    private String filename;
    private String filepath;
    private String fullpath;
    @Enumerated(EnumType.STRING)
    private FileType type;
    private String creatorId;
}
