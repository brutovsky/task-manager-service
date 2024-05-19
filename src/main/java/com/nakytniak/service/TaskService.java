package com.nakytniak.service;

import com.google.dataflow.v1beta3.Job;
import com.nakytniak.dto.DataflowJobInfo;
import com.nakytniak.dto.DataflowTaskDto;
import com.nakytniak.entity.FileEntry;
import com.nakytniak.entity.FileType;
import com.nakytniak.entity.SchoolEntity;
import com.nakytniak.entity.TaskEntity;
import com.nakytniak.entity.TaskEntityType;
import com.nakytniak.entity.TaskStatus;
import com.nakytniak.entity.TaskType;
import com.nakytniak.exception.EntityNotFoundException;
import com.nakytniak.exception.GlobalExceptionHandler;
import com.nakytniak.repository.FileEntryRepository;
import com.nakytniak.repository.SchoolRepository;
import com.nakytniak.repository.TaskRepository;
import com.nakytniak.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.dataflow.v1beta3.JobState.JOB_STATE_DONE;
import static com.google.dataflow.v1beta3.JobState.JOB_STATE_PENDING;
import static com.google.dataflow.v1beta3.JobState.JOB_STATE_RUNNING;
import static com.nakytniak.entity.TaskStatus.RUNNING;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());

    private final TaskRepository taskRepository;

    private final SchoolRepository schoolRepository;

    private final DataflowService dataflowService;
    private final FileEntryRepository fileEntryRepository;

    public DataflowTaskDto createTask(final TaskType type, final TaskEntityType entityType, final String schoolId,
            final String filename) {
        final SchoolEntity school = schoolRepository.findBySchoolId(schoolId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("School %s does not exists", schoolId)));
        final FileEntry file = fileEntryRepository.findBySchoolIdAndFilename(school.getId(), filename)
                .orElseThrow(() -> new EntityNotFoundException(String.format("File %s does not exists", filename)));

        TaskEntity taskEntity = new TaskEntity();
        final String jobName = Utils.formTemplateJobName(type, schoolId);
        taskEntity.setType(type);
        taskEntity.setStatus(TaskStatus.PENDING);
        taskEntity.setDataflowJobName(jobName);
        taskEntity.setSchoolId(school.getId());

        try {
            final Map<String, String> metadata = getMetadata(type, entityType, file, school);
            taskEntity.setMetadata(metadata);
            taskEntity = taskRepository.save(taskEntity);

            final String jobId = triggerJob(taskEntity);
            taskEntity.setDataflowJobId(jobId);
            taskEntity.setStatus(RUNNING);
            taskEntity = taskRepository.save(taskEntity);

            return mapTask(taskEntity, schoolId);
        } catch (final Exception exception) {
            taskEntity.setStatus(TaskStatus.FAILED);
            taskEntity = taskRepository.save(taskEntity);
            return mapTask(taskEntity, schoolId);
        }
    }

    private String triggerJob(final TaskEntity taskEntity) throws IOException {
        return switch (taskEntity.getType()) {
            case CSV_TO_FIRESTORE -> dataflowService.triggerDataflowCsvJob(taskEntity);
            case SQL_TO_FIRESTORE -> dataflowService.triggerDataflowMySqlJob(taskEntity);
        };
    }

    private Map<String, String> getMetadata(final TaskType type, final TaskEntityType entityType,
            final FileEntry file, final SchoolEntity school) {
        return switch (type) {
            case CSV_TO_FIRESTORE -> {
                checkFileExtension(file.getType(), FileType.CSV);
                yield dataflowService.getMetadataForCsvJob(entityType, school.getSchoolId(), file.getFullpath());
            }
            case SQL_TO_FIRESTORE -> {
                checkFileExtension(file.getType(), FileType.JSON);
                yield dataflowService.getMetadataForSqlJob(entityType, school.getSchoolId(), file.getFullpath());
            }
        };
    }

    private void checkFileExtension(final FileType actualFileType, final FileType expectedFileType) {
        if (actualFileType != expectedFileType) {
            throw new IllegalArgumentException(String.format("Wrong file extension [%s], [%s] expected",
                    actualFileType, expectedFileType.getExt()));
        }
    }

    public DataflowTaskDto getTask(final Integer taskId, final String schoolId) throws IOException {
        final SchoolEntity school = schoolRepository.findBySchoolId(schoolId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("School %s does not exists", schoolId)));
        final TaskEntity taskEntity = taskRepository.findByIdAndSchoolId(taskId, school.getId())
                .orElseThrow(() -> new EntityNotFoundException(TaskEntity.class, String.valueOf(taskId)));
        final Job jobInfo = dataflowService.getDataflowJobInfo(taskEntity.getDataflowJobId());
        final DataflowTaskDto dataflowTaskDto = mapTask(taskEntity, schoolId);
        dataflowTaskDto.setJobInfo(mapJobInfo(jobInfo));
        return dataflowTaskDto;
    }

    private DataflowJobInfo mapJobInfo(final Job jobInfo) {
        return DataflowJobInfo.builder()
                .jobId(jobInfo.getId())
                .jobName(jobInfo.getName())
                .jobType(jobInfo.getType().name())
                .currentState(jobInfo.getCurrentState().name())
                .createDateTime(
                        Instant.ofEpochSecond(jobInfo.getCreateTime().getSeconds(), jobInfo.getCreateTime().getNanos())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime())
                .startDateTime(
                        Instant.ofEpochSecond(jobInfo.getStartTime().getSeconds(), jobInfo.getStartTime().getNanos())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime())
                .stagesCount(jobInfo.getExecutionInfo().getStagesCount())
                .build();
    }

    public List<DataflowTaskDto> getTasks(final String schoolId) {
        final SchoolEntity school = schoolRepository.findBySchoolId(schoolId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("School %s does not exists", schoolId)));
        final List<TaskEntity> taskEntities = taskRepository.findAllBySchoolId(school.getId());
        return taskEntities.stream().map(taskEntity -> mapTask(taskEntity, schoolId)).toList();
    }

    public void updateTaskStatus(final Integer taskId, final TaskStatus status, final String schoolId) {
        final SchoolEntity school = schoolRepository.findBySchoolId(schoolId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("School %s does not exists", schoolId)));
        final TaskEntity taskEntity = taskRepository.findByIdAndSchoolId(taskId, school.getId())
                .orElseThrow(() -> new EntityNotFoundException(TaskEntity.class, String.valueOf(taskId)));
        taskEntity.setStatus(status);
        taskRepository.save(taskEntity);
    }

    public void checkAndUpdateJobStatuses() {
        final List<TaskEntity> tasks = taskRepository.findAllByStatusIn(Set.of(RUNNING));

        for (TaskEntity task : tasks) {
            try {
                switch (task.getStatus()) {
                    case RUNNING -> {
                        final Job jobInfo = dataflowService.getDataflowJobInfo(task.getDataflowJobId());
                        if (jobInfo.getCurrentState() == JOB_STATE_DONE) {
                            logger.log(Level.INFO, String.format("Task %s successfully finished", task.getId()));
                            task.setStatus(TaskStatus.FINISHED);
                        } else if (!(jobInfo.getCurrentState() == JOB_STATE_RUNNING ||
                                jobInfo.getCurrentState() == JOB_STATE_PENDING)) {
                            logger.log(Level.INFO, String.format("Task %s failed with state %s", task.getId(),
                                    jobInfo.getCurrentState().name()));
                            task.setStatus(TaskStatus.FAILED);
                        } else {
                            logger.log(Level.INFO, String.format("Task %s is still running %s", task.getId(),
                                    jobInfo.getCurrentState().name()));
                        }
                    }
                    case PENDING -> {
                        final String jobId = triggerJob(task);
                        task.setDataflowJobId(jobId);
                        task.setStatus(RUNNING);
                        logger.log(Level.INFO, String.format("Task %s has started", task.getId()));
                    }
                }
            } catch (final Exception e) {
                task.setStatus(TaskStatus.FAILED);
                e.printStackTrace();
                logger.log(Level.SEVERE, "Error with task {0}: {1}", new Object[]{task.getId(), e.getMessage()});
            }
        }

        taskRepository.saveAll(tasks);
    }

    private DataflowTaskDto mapTask(final TaskEntity taskEntity, final String schoolId) {
        return DataflowTaskDto.builder()
                .taskId(taskEntity.getId())
                .status(taskEntity.getStatus())
                .type(taskEntity.getType())
                .dataflowJobId(taskEntity.getDataflowJobId())
                .dataflowJobName(taskEntity.getDataflowJobName())
                .schoolId(schoolId)
                .createdDate(taskEntity.getCreatedDate())
                .updatedDate(taskEntity.getUpdatedDate())
                .build();
    }
}
