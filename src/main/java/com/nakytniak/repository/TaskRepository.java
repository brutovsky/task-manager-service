package com.nakytniak.repository;

import com.nakytniak.entity.TaskEntity;
import com.nakytniak.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Integer> {
    Optional<TaskEntity> findByIdAndSchoolId(Integer taskId, Integer schoolId);

    List<TaskEntity> findAllBySchoolId(Integer schoolId);

    List<TaskEntity> findAllByStatusIn(Set<TaskStatus> statuses);
}