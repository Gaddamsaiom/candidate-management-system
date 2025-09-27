package com.candidatemanagement.exception;

import com.candidatemanagement.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleCandidateNotFoundException_returns404() {
        ResponseEntity<ApiResponse<Void>> resp = handler.handleCandidateNotFoundException(new CandidateNotFoundException("x"));
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void handleDuplicateEmailException_returns409() {
        ResponseEntity<ApiResponse<Void>> resp = handler.handleDuplicateEmailException(new DuplicateEmailException("x"));
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void handleIOException_returns500() {
        ResponseEntity<ApiResponse<Void>> resp = handler.handleIOException(new IOException("io"));
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void handleIllegalArgument_returns400() {
        ResponseEntity<ApiResponse<Void>> resp = handler.handleIllegalArgumentException(new IllegalArgumentException("bad"));
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void handleGeneric_returns500() {
        ResponseEntity<ApiResponse<Void>> resp = handler.handleGenericException(new RuntimeException("x"));
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
