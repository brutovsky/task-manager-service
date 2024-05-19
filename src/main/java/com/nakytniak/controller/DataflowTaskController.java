package com.nakytniak.controller;

import com.nakytniak.dto.CoreResponse;
import com.nakytniak.dto.DataflowTaskDto;
import com.nakytniak.entity.TaskEntityType;
import com.nakytniak.entity.TaskStatus;
import com.nakytniak.entity.TaskType;
import com.nakytniak.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class DataflowTaskController {

    private final TaskService taskService;

    @PostMapping()
    public ResponseEntity<CoreResponse<DataflowTaskDto>> createTask(@RequestParam final TaskType type,
            @RequestParam final TaskEntityType entityType, @RequestParam final String schoolId,
            @RequestParam final String filename) {
        return ResponseEntity.ok(CoreResponse.of(taskService.createTask(type, entityType, schoolId, filename)));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<CoreResponse<DataflowTaskDto>> getTask(@PathVariable final Integer taskId,
            @RequestParam final String schoolId) throws IOException {
        return ResponseEntity.ok(CoreResponse.of(taskService.getTask(taskId, schoolId)));
    }

    @GetMapping()
    public ResponseEntity<CoreResponse<List<DataflowTaskDto>>> getTasks(@RequestParam final String schoolId) {
        return ResponseEntity.ok(CoreResponse.of(taskService.getTasks(schoolId)));
    }

    @PostMapping("/{taskId}/status")
    public void updateTaskStatus(@PathVariable final Integer taskId, @RequestParam final TaskStatus status,
            @RequestParam final String schoolId) {
        taskService.updateTaskStatus(taskId, status, schoolId);
    }
}

