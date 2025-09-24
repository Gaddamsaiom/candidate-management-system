package com.candidatemanagement.dto;

import com.candidatemanagement.entity.CandidateStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusUpdateRequest {
    
    @NotNull(message = "Status is required")
    private CandidateStatus status;
}
