package com.candidatemanagement.controller;

import com.candidatemanagement.dto.ApiResponse;
import com.candidatemanagement.dto.CandidateResponse;
import com.candidatemanagement.dto.CandidateSubmissionRequest;
import com.candidatemanagement.service.CandidateService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/freshers")
@RequiredArgsConstructor
@Validated
public class FresherController {

    private final CandidateService candidateService;

    @PostMapping(value = "/submit", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<CandidateResponse>> submitFresher(
            @RequestParam @NotBlank String name,
            @RequestParam @Email String email,
            @RequestParam @NotBlank String phone,
            @RequestParam(required = false) String qualification,
            @RequestParam(required = false) String skills,
            @RequestPart(name = "resume", required = false) MultipartFile resume
    ) throws IOException {
        CandidateSubmissionRequest request = CandidateSubmissionRequest.builder()
                .role("FRESHER")
                .name(name)
                .email(email)
                .phone(phone)
                .qualification(qualification)
                .skills(skills)
                .build();

        CandidateResponse response = candidateService.submitCandidate(request, resume);
        return ResponseEntity.ok(ApiResponse.success("Fresher submitted", response));
    }
}
