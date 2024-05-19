-- liquibase formatted sql
-- changeset brutovsky:init.sql

CREATE TABLE IF NOT EXISTS school
(
    id           INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    school_id    VARCHAR(255)                        NOT NULL,
    name         VARCHAR(255)                        NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS task
(
    id                INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    status            VARCHAR(255)                        NOT NULL,
    type              VARCHAR(255)                        NOT NULL,
    dataflow_job_id   VARCHAR(255)                        NULL,
    dataflow_job_name VARCHAR(255)                        NOT NULL,
    school_id         INT UNSIGNED                        NOT NULL,
    metadata          JSON                                NOT NULL,
    created_date      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_date      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `task__school__id` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`)
);

CREATE TABLE IF NOT EXISTS file
(
    id           INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    school_id    INT UNSIGNED,
    filename     VARCHAR(255)  NOT NULL,
    filepath     VARCHAR(1024) NOT NULL,
    fullpath     VARCHAR(1024) NOT NULL,
    type         VARCHAR(255)  NOT NULL,
    creator_id   VARCHAR(255),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `file__school__id` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`)
);

CREATE TABLE school_request
(
    id           INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    full_name          VARCHAR(1024) NOT NULL,
    phone_number       VARCHAR(20)  NOT NULL,
    email              VARCHAR(255) NOT NULL,
    number_of_students INT          NOT NULL,
    region             VARCHAR(255) NOT NULL,
    address            VARCHAR(255) NOT NULL,
    type_of_education  VARCHAR(255) NOT NULL,
    status             VARCHAR(255) NOT NULL,
    created_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
