package com.candidatemanagement.repository;

import com.candidatemanagement.entity.Candidate;
import com.candidatemanagement.entity.CandidateStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JsonCandidateStorageTest {

    private Path tempFile;
    private JsonCandidateStorage storage;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("candidates", ".json");
        Files.writeString(tempFile, "[]");
        storage = new JsonCandidateStorage(tempFile.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    void save_findAll_and_counts_work() {
        Candidate c = Candidate.builder().role("FRESHER").name("X").email("x@x.com").phone("9999999999").status(CandidateStatus.SUBMITTED).build();
        storage.save(c);
        List<Candidate> all = storage.findAll();
        assertThat(all).hasSize(1);
        assertThat(storage.count()).isEqualTo(1);
        assertThat(storage.existsByEmail("x@x.com")).isTrue();
        assertThat(storage.findByRole("FRESHER")).hasSize(1);
        assertThat(storage.findByStatus(CandidateStatus.SUBMITTED)).hasSize(1);
    }
}
