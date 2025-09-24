package com.candidatemanagement.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateStatistics {
    
    private long totalCandidates;
    private long freshers;
    private long experienced;
    private long submitted;
    private long underReview;
    private long shortlisted;
    private long selected;
    private long rejected;
}
