package com.nakytniak.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Data
public class DataflowJobInfo {
    final String jobId;
    final String currentState;
    final LocalDateTime createDateTime;
    final LocalDateTime startDateTime;
    final Integer stagesCount;
    final String jobName;
    final String jobType;
}
