package com.candidatemanagement.entity;

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
    
    public String getDisplayName() {
        return displayName;
    }
}
