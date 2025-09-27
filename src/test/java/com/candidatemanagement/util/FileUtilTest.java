package com.candidatemanagement.util;

import com.candidatemanagement.entity.Candidate;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FileUtilTest {

    private final FileUtil util = new FileUtil();

    @Test
    void base64_roundtrip() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getBytes()).thenReturn(new byte[]{1,2,3});

        String b64 = util.convertToBase64(file);
        assertThat(Base64.getDecoder().decode(b64)).containsExactly(1,2,3);

        byte[] bytes = util.convertFromBase64(b64);
        assertThat(bytes).containsExactly(1,2,3);
    }

    @Test
    void isValidResumeFile_checksContentTypeAndExtension() {
        MultipartFile pdf = mock(MultipartFile.class);
        when(pdf.isEmpty()).thenReturn(false);
        when(pdf.getContentType()).thenReturn("application/pdf");
        when(pdf.getOriginalFilename()).thenReturn("a.pdf");
        assertThat(util.isValidResumeFile(pdf)).isTrue();

        MultipartFile bad = mock(MultipartFile.class);
        when(bad.isEmpty()).thenReturn(false);
        when(bad.getContentType()).thenReturn("text/plain");
        when(bad.getOriginalFilename()).thenReturn("a.txt");
        assertThat(util.isValidResumeFile(bad)).isFalse();
    }

    @Test
    void json_conversion() throws IOException {
        List<Candidate> list = List.of(Candidate.builder().id(1L).name("A").email("a@b.com").phone("9999999999").role("FRESHER").build());
        String json = util.convertCandidatesToJson(list);
        List<Candidate> back = util.convertJsonToCandidates(json);
        assertThat(back).hasSize(1);
        assertThat(back.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void isValidCandidateJson_handlesInvalid() {
        assertThat(util.isValidCandidateJson("not json")).isFalse();
    }
}
