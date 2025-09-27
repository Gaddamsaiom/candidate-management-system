package com.candidatemanagement.controller;

import com.candidatemanagement.dto.CandidateResponse;
import com.candidatemanagement.service.CandidateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExperiencedController.class)
class ExperiencedControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CandidateService candidateService;

    @Test
    void submitExperienced_ok() throws Exception {
        CandidateResponse resp = CandidateResponse.builder().id(2L).name("Jane").build();
        given(candidateService.submitCandidate(any(), any())).willReturn(resp);
        MockMultipartFile resume = new MockMultipartFile("resume", "resume.pdf", "application/pdf", new byte[]{1});

        mvc.perform(multipart("/api/experienced/submit")
                        .file(resume)
                        .param("name", "Jane")
                        .param("email", "jane@example.com")
                        .param("phone", "9999999999")
                        .param("experience", "5 years")
                        .param("skills", "Spring")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(2));
    }
}
