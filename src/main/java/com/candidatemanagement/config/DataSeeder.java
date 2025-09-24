package com.candidatemanagement.config;

import com.candidatemanagement.entity.Candidate;
import com.candidatemanagement.entity.CandidateStatus;
import com.candidatemanagement.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements ApplicationRunner {

    private final CandidateRepository candidateRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (candidateRepository.count() > 0) {
            log.info("Database already seeded");
            return;
        }

        String sampleResume = Base64.getEncoder().encodeToString("Sample Resume PDF content".getBytes(StandardCharsets.UTF_8));

        List<Candidate> candidates = List.of(
                Candidate.builder().role("FRESHER").name("Alice Fresh").email("alice@example.com").phone("9999999999").qualification("B.Tech").skills("Java, Spring").status(CandidateStatus.SUBMITTED).resume(sampleResume).resumeFilename("alice.pdf").build(),
                Candidate.builder().role("FRESHER").name("Bob New").email("bob@example.com").phone("9888888888").qualification("MCA").skills("React, JS").status(CandidateStatus.UNDER_REVIEW).resume(sampleResume).resumeFilename("bob.pdf").build(),
                Candidate.builder().role("EXPERIENCED").name("Carol Pro").email("carol@example.com").phone("9777777777").experience("5 years at ACME").skills("Java, AWS").status(CandidateStatus.SHORTLISTED).resume(sampleResume).resumeFilename("carol.pdf").build(),
                Candidate.builder().role("EXPERIENCED").name("Dave Senior").email("dave@example.com").phone("9666666666").experience("7 years at Beta").skills("Spring Boot, Docker").status(CandidateStatus.INTERVIEWED).resume(sampleResume).resumeFilename("dave.pdf").build()
        );

        candidateRepository.saveAll(candidates);
        log.info("Seeded {} candidates", candidates.size());
    }
}
