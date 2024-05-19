package com.nakytniak.repository;

import com.nakytniak.entity.SchoolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<SchoolEntity, Integer> {
    Optional<SchoolEntity> findBySchoolId(final String schoolId);
}
