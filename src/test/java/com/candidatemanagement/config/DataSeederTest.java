package com.candidatemanagement.config;

import com.candidatemanagement.entity.Candidate;
import com.candidatemanagement.entity.CandidateStatus;
import com.candidatemanagement.repository.CandidateStorage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class DataSeederTest {

    @Test
    void run_seeds_when_empty() throws Exception {
        CandidateStorage storage = mock(CandidateStorage.class);
        given(storage.count()).willReturn(0L);
        given(storage.saveAll(anyList())).willAnswer(invocation -> invocation.getArgument(0));

        DataSeeder seeder = new DataSeeder(storage);
        seeder.run(null);

        verify(storage).saveAll(anyList());
    }

    @Test
    void run_skips_when_not_empty() throws Exception {
        CandidateStorage storage = mock(CandidateStorage.class);
        given(storage.count()).willReturn(5L);
        DataSeeder seeder = new DataSeeder(storage);
        seeder.run(null);
        verify(storage, never()).saveAll(anyList());
    }
}
