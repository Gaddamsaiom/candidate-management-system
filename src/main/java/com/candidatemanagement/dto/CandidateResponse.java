package com.candidatemanagement.dto;

import com.candidatemanagement.entity.CandidateStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateResponse {
    
    private Long id;
    private String role;
    private String name;
    private String email;
    private String phone;
    private String qualification;
    private String experience;
    private String skills;
    private CandidateStatus status;
    private String resumeFilename;
    private boolean hasResume;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
