package com.candidatemanagement.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum CandidateStatus {
    SUBMITTED("Submitted"),
    UNDER_REVIEW("Under Review"),
    SHORTLISTED("Shortlisted"),
    INTERVIEW_SCHEDULED("Interview Scheduled"),
    INTERVIEWED("Interviewed"),
    SELECTED("Selected"),
    REJECTED("Rejected"),
    ON_HOLD("On Hold");
    
    private final String displayName;
    
    CandidateStatus(String displayName) {
        this.displayName = displayName;
    }
    
    @JsonIgnore
    public String getDisplayName() {
        return displayName;
    }
}
