package com.candidatemanagement.controller;

import com.candidatemanagement.dto.ApiResponse;
import com.candidatemanagement.dto.CandidateResponse;
import com.candidatemanagement.service.CandidateService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FresherController.class)
class FresherControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CandidateService candidateService;

    @Test
    void submitFresher_returnsOk() throws Exception {
        CandidateResponse resp = CandidateResponse.builder().id(1L).name("John").build();
        given(candidateService.submitCandidate(any(), any())).willReturn(resp);
        MockMultipartFile resume = new MockMultipartFile("resume", "resume.pdf", "application/pdf", new byte[]{1});

        mvc.perform(multipart("/api/freshers/submit")
                        .file(resume)
                        .param("name", "John")
                        .param("email", "john@example.com")
                        .param("phone", "9999999999")
                        .param("qualification", "B.Tech")
                        .param("skills", "Java")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }
}
