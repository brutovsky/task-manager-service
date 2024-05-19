package com.nakytniak.repository;

import com.nakytniak.entity.FileEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileEntryRepository extends JpaRepository<FileEntry, Integer> {
    List<FileEntry> findAllBySchoolId(final Integer schoolId);

    Optional<FileEntry> findBySchoolIdAndFilename(final Integer schoolId, final String filename);
}
