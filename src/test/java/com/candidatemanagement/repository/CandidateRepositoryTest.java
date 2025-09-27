package com.candidatemanagement.repository;

import com.candidatemanagement.entity.Candidate;
import com.candidatemanagement.entity.CandidateStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CandidateRepositoryTest {

    @Autowired
    private CandidateRepository repo;

    @Test
    void save_and_query_methods_work() {
        Candidate a = Candidate.builder().role("FRESHER").name("Alice").email("alice@x.com").phone("9999999999").skills("Java").status(CandidateStatus.SUBMITTED).build();
        Candidate b = Candidate.builder().role("EXPERIENCED").name("Bob").email("bob@x.com").phone("9999999998").skills("AWS").status(CandidateStatus.SELECTED).build();
        repo.saveAll(List.of(a, b));

        assertThat(repo.findByEmail("alice@x.com")).isPresent();
        assertThat(repo.findByRole("FRESHER")).hasSize(1);
        assertThat(repo.findByStatus(CandidateStatus.SELECTED)).hasSize(1);
        assertThat(repo.countByRole("FRESHER")).isEqualTo(1);
        assertThat(repo.countByStatus(CandidateStatus.SUBMITTED)).isEqualTo(1);

        assertThat(repo.searchCandidates("ali")).hasSize(1);
        assertThat(repo.findCandidatesByCriteria("EXPERIENCED", CandidateStatus.SELECTED, null)).hasSize(1);
    }
}
