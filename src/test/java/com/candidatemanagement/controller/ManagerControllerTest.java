package com.candidatemanagement.controller;

import com.candidatemanagement.dto.CandidateResponse;
import com.candidatemanagement.dto.StatusUpdateRequest;
import com.candidatemanagement.entity.CandidateStatus;
import com.candidatemanagement.service.CandidateService;
import com.candidatemanagement.service.CandidateStatistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ManagerController.class)
class ManagerControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CandidateService candidateService;

    @Test
    void getAll_returnsList() throws Exception {
        given(candidateService.getAllCandidates()).willReturn(List.of(
                CandidateResponse.builder().id(1L).build(),
                CandidateResponse.builder().id(2L).build()
        ));
        mvc.perform(get("/api/manager"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)));
    }

    @Test
    void getById_returnsItem() throws Exception {
        given(candidateService.getCandidateById(1L)).willReturn(CandidateResponse.builder().id(1L).build());
        mvc.perform(get("/api/manager/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void search_callsService() throws Exception {
        given(candidateService.searchCandidates(any(), any(), any())).willReturn(List.of());
        mvc.perform(get("/api/manager/search").param("role", "FRESHER"))
                .andExpect(status().isOk());
    }

    @Test
    void updateStatus_callsService() throws Exception {
        given(candidateService.updateCandidateStatus(eq(1L), eq(CandidateStatus.SELECTED)))
                .willReturn(CandidateResponse.builder().id(1L).status(CandidateStatus.SELECTED).build());
        String body = "{\"status\":\"SELECTED\"}";
        mvc.perform(patch("/api/manager/1/status").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SELECTED"));
    }

    @Test
    void delete_callsService() throws Exception {
        mvc.perform(delete("/api/manager/1"))
                .andExpect(status().isOk());
    }

    @Test
    void stats_returnsData() throws Exception {
        CandidateStatistics stats = CandidateStatistics.builder().totalCandidates(10).build();
        given(candidateService.getCandidateStatistics()).willReturn(stats);
        mvc.perform(get("/api/manager/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalCandidates").value(10));
    }
}
