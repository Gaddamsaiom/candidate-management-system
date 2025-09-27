package com.candidatemanagement.service;

import com.candidatemanagement.dto.CandidateResponse;
import com.candidatemanagement.dto.CandidateSubmissionRequest;
import com.candidatemanagement.entity.Candidate;
import com.candidatemanagement.entity.CandidateStatus;
import com.candidatemanagement.exception.CandidateNotFoundException;
import com.candidatemanagement.exception.DuplicateEmailException;
import com.candidatemanagement.repository.CandidateStorage;
import com.candidatemanagement.util.FileUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {

    @Mock
    private CandidateStorage candidateStorage;

    @Mock
    private FileUtil fileUtil;

    @Mock
    private MultipartFile resumeFile;

    @InjectMocks
    private CandidateService service;

    private CandidateSubmissionRequest baseRequest;

    @BeforeEach
    void setUp() {
        baseRequest = CandidateSubmissionRequest.builder()
                .role("FRESHER")
                .name("John Doe")
                .email("john@example.com")
                .phone("9999999999")
                .qualification("B.Tech")
                .skills("Java")
                .build();
    }

    @Test
    void submitCandidate_success_withResume() throws IOException {
        given(candidateStorage.existsByEmail("john@example.com")).willReturn(false);
        given(resumeFile.isEmpty()).willReturn(false);
        given(fileUtil.isValidResumeFile(resumeFile)).willReturn(true);
        given(fileUtil.convertToBase64(resumeFile)).willReturn("BASE64");
        Candidate saved = Candidate.builder()
                .id(1L)
                .role("FRESHER")
                .name("John Doe")
                .email("john@example.com")
                .phone("9999999999")
                .qualification("B.Tech")
                .skills("Java")
                .status(CandidateStatus.SUBMITTED)
                .resume("BASE64")
                .resumeFilename("resume.pdf")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        given(candidateStorage.save(any())).willReturn(saved);

        CandidateResponse resp = service.submitCandidate(baseRequest, resumeFile);

        assertThat(resp.getId()).isEqualTo(1L);
        assertThat(resp.isHasResume()).isTrue();
        ArgumentCaptor<Candidate> captor = ArgumentCaptor.forClass(Candidate.class);
        verify(candidateStorage).save(captor.capture());
        Candidate toSave = captor.getValue();
        assertThat(toSave.getEmail()).isEqualTo("john@example.com");
        assertThat(toSave.getResume()).isEqualTo("BASE64");
    }

    @Test
    void submitCandidate_duplicateEmail_throws() {
        given(candidateStorage.existsByEmail("john@example.com")).willReturn(true);
        assertThatThrownBy(() -> service.submitCandidate(baseRequest, null))
                .isInstanceOf(DuplicateEmailException.class);
        verify(candidateStorage, never()).save(any());
    }

    @Test
    void submitCandidate_invalidResume_throws() {
        given(candidateStorage.existsByEmail("john@example.com")).willReturn(false);
        given(resumeFile.isEmpty()).willReturn(false);
        given(fileUtil.isValidResumeFile(resumeFile)).willReturn(false);
        assertThatThrownBy(() -> service.submitCandidate(baseRequest, resumeFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid resume file format");
    }

    @Test
    void getCandidateById_success() {
        Candidate c = Candidate.builder().id(10L).email("a@b.com").role("FRESHER").name("A").phone("9999999999").build();
        given(candidateStorage.findById(10L)).willReturn(Optional.of(c));
        CandidateResponse resp = service.getCandidateById(10L);
        assertThat(resp.getId()).isEqualTo(10L);
    }

    @Test
    void getCandidateById_notFound_throws() {
        given(candidateStorage.findById(99L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> service.getCandidateById(99L))
                .isInstanceOf(CandidateNotFoundException.class);
    }

    @Test
    void updateCandidateStatus_success() {
        Candidate c = Candidate.builder().id(5L).status(CandidateStatus.SUBMITTED).role("FRESHER").name("N").email("e@e").phone("9999999999").build();
        given(candidateStorage.findById(5L)).willReturn(Optional.of(c));
        given(candidateStorage.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        CandidateResponse resp = service.updateCandidateStatus(5L, CandidateStatus.SELECTED);
        assertThat(resp.getStatus()).isEqualTo(CandidateStatus.SELECTED);
    }

    @Test
    void deleteCandidate_notExists_throws() {
        given(candidateStorage.existsById(1L)).willReturn(false);
        assertThatThrownBy(() -> service.deleteCandidate(1L))
                .isInstanceOf(CandidateNotFoundException.class);
    }

    @Test
    void getCandidateResume_success() {
        Candidate c = Candidate.builder().id(2L).resume("BASE64").build();
        given(candidateStorage.findById(2L)).willReturn(Optional.of(c));
        given(fileUtil.convertFromBase64("BASE64")).willReturn(new byte[]{1,2,3});
        byte[] bytes = service.getCandidateResume(2L);
        assertThat(bytes).containsExactly(1,2,3);
    }

    @Test
    void getCandidateResume_missing_throws() {
        Candidate c = Candidate.builder().id(2L).resume("").build();
        given(candidateStorage.findById(2L)).willReturn(Optional.of(c));
        assertThatThrownBy(() -> service.getCandidateResume(2L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void exportCandidatesToJson_callsFileUtil() throws IOException {
        given(candidateStorage.findAll()).willReturn(List.of());
        given(fileUtil.convertCandidatesToJson(anyList())).willReturn("[]");
        String json = service.exportCandidatesToJson();
        assertThat(json).isEqualTo("[]");
        verify(fileUtil).convertCandidatesToJson(anyList());
    }

    @Test
    void importCandidatesFromJson_validatesAndSaves() throws IOException {
        given(fileUtil.isValidCandidateJson("json")).willReturn(true);
        Candidate c = Candidate.builder().id(1L).build();
        given(fileUtil.convertJsonToCandidates("json")).willReturn(List.of(c));
        given(candidateStorage.saveAll(anyList())).willReturn(List.of(c));
        List<CandidateResponse> resp = service.importCandidatesFromJson("json");
        assertThat(resp).hasSize(1);
        verify(candidateStorage).saveAll(anyList());
    }

    @Test
    void importCandidatesFromJson_invalid_throws() {
        given(fileUtil.isValidCandidateJson("bad")).willReturn(false);
        assertThatThrownBy(() -> service.importCandidatesFromJson("bad"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getCandidateStatistics_aggregates() {
        given(candidateStorage.count()).willReturn(10L);
        given(candidateStorage.countByRole("FRESHER")).willReturn(6L);
        given(candidateStorage.countByRole("EXPERIENCED")).willReturn(4L);
        given(candidateStorage.countByStatus(any())).willReturn(2L);
        CandidateStatistics stats = service.getCandidateStatistics();
        assertThat(stats.getTotalCandidates()).isEqualTo(10L);
        assertThat(stats.getFreshers()).isEqualTo(6L);
        assertThat(stats.getExperienced()).isEqualTo(4L);
    }
}
