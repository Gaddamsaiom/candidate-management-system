package com.candidatemanagement.config;

import com.candidatemanagement.CandidateManagementSystemApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CandidateManagementSystemApplication.class)
class ContextLoadTest {

    @Test
    void contextLoads() {
        // Verifies Spring context starts
    }
}
