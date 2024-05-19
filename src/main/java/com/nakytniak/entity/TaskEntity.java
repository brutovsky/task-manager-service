package com.nakytniak.entity;

import com.nakytniak.utils.HashMapConverter;
import jakarta.persistence.Convert;
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

import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "task")
public class TaskEntity extends CommonEntity {
    @Enumerated(value = EnumType.STRING)
    private TaskStatus status;
    @Enumerated(value = EnumType.STRING)
    private TaskType type;
    private String dataflowJobId;
    private String dataflowJobName;
    private Integer schoolId;
    @Convert(converter = HashMapConverter.class)
    private Map<String, String> metadata;
}
