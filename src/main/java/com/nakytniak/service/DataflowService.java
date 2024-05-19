package com.nakytniak.service;

import com.google.dataflow.v1beta3.CreateJobFromTemplateRequest;
import com.google.dataflow.v1beta3.GetJobRequest;
import com.google.dataflow.v1beta3.Job;
import com.google.dataflow.v1beta3.JobView;
import com.google.dataflow.v1beta3.JobsV1Beta3Client;
import com.google.dataflow.v1beta3.RuntimeEnvironment;
import com.google.dataflow.v1beta3.TemplatesServiceClient;
import com.google.dataflow.v1beta3.TemplatesServiceSettings;
import com.nakytniak.entity.TaskEntity;
import com.nakytniak.entity.TaskEntityType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DataflowService {
    @Value("${gcp.projectId}")
    private String projectId;

    @Value("${gcp.location}")
    private String location;

    @Value("${dataflow.pipeline.template.csv-to-firestore.location}")
    private String csvToFirestoreTemplateLocation;

    @Value("${dataflow.pipeline.template.mysql-to-firestore.location}")
    private String mysqlToFirestoreTemplateLocation;

    @Value("${dataflow.pipeline.template.mysql-to-firestore.mysql.instance.connectionurl}")
    private String testMysqlInstanceConnectionUrlSecret;

    @Value("${dataflow.pipeline.template.mysql-to-firestore.mysql.instance.username}")
    private String testMysqlInstanceUsernameSecret;

    @Value("${dataflow.pipeline.template.mysql-to-firestore.mysql.instance.password}")
    private String testMysqlInstancePasswordSecret;

    private static final int MAX_WORKERS = 1;

    public Map<String, String> getMetadataForCsvJob(final TaskEntityType entityType, final String schoolId,
            final String csvLocation) {
        final Map<String, String> metadata = new HashMap<>();
        metadata.put("entityName", entityType.name());
        metadata.put("project", projectId);
        metadata.put("csvFileLocation", csvLocation);
        metadata.put("maxNumWorkers", "1");
        metadata.put("firestoreCollectionName", entityType.getCollectionName());
        metadata.put("databaseId", schoolId);
        return metadata;
    }

    public Map<String, String> getMetadataForSqlJob(final TaskEntityType entityType, final String schoolId,
            final String mappingLocation) {
        final Map<String, String> metadata = new HashMap<>();
        metadata.put("entityName", entityType.name());
        metadata.put("project", projectId);
        metadata.put("mappingLocation", mappingLocation);
        metadata.put("maxNumWorkers", Integer.toString(MAX_WORKERS));
        metadata.put("firestoreCollectionName", entityType.getCollectionName());
        metadata.put("databaseId", schoolId);
        metadata.put("mySqlConnectionUrl", testMysqlInstanceConnectionUrlSecret);
        metadata.put("mySqlUsername", testMysqlInstanceUsernameSecret);
        metadata.put("mySqlPassword", testMysqlInstancePasswordSecret);
        return metadata;
    }

    public String triggerDataflowCsvJob(final TaskEntity taskEntity) throws IOException {
        return triggerDataflowJob(taskEntity, csvToFirestoreTemplateLocation);
    }

    public String triggerDataflowMySqlJob(final TaskEntity taskEntity) throws IOException {
        return triggerDataflowJob(taskEntity, mysqlToFirestoreTemplateLocation);
    }

    private String triggerDataflowJob(final TaskEntity taskEntity, final String templateLocations) throws IOException {
        final TemplatesServiceSettings templatesServiceSettings = TemplatesServiceSettings.newHttpJsonBuilder().build();

        try (TemplatesServiceClient templatesServiceClient = TemplatesServiceClient.create(templatesServiceSettings)) {
            CreateJobFromTemplateRequest request = CreateJobFromTemplateRequest.newBuilder()
                    .setProjectId(projectId)
                    .setJobName(taskEntity.getDataflowJobName())
                    .setGcsPath(templateLocations)
                    .setLocation(location)
                    .putAllParameters(taskEntity.getMetadata())
                    .setEnvironment(RuntimeEnvironment.newBuilder().build())
                    .build();

            Job response = templatesServiceClient.createJobFromTemplate(request);
            return response.getId();
        }
    }

    public Job getDataflowJobInfo(final String jobId) throws IOException {
        try (JobsV1Beta3Client jobsV1Beta3Client = JobsV1Beta3Client.create()) {
            GetJobRequest request = GetJobRequest.newBuilder()
                    .setProjectId(projectId)
                    .setJobId(jobId)
                    .setView(JobView.forNumber(0))
                    .setLocation(location)
                    .build();
            return jobsV1Beta3Client.getJob(request);
        }
    }

}
