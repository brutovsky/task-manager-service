package com.nakytniak.controller;

import com.nakytniak.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scheduled")
public class ScheduledController {

    private final TaskService taskService;

    @PostMapping("/update-tasks-status")
    public ResponseEntity<Void> updateTasksStatus() {
        taskService.checkAndUpdateJobStatuses();
        return ResponseEntity.ok().build();
    }

}
