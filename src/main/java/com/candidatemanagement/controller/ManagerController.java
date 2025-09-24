package com.candidatemanagement.controller;

import com.candidatemanagement.dto.ApiResponse;
import com.candidatemanagement.dto.CandidateResponse;
import com.candidatemanagement.dto.StatusUpdateRequest;
import com.candidatemanagement.entity.CandidateStatus;
import com.candidatemanagement.service.CandidateService;
import com.candidatemanagement.service.CandidateStatistics;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final CandidateService candidateService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CandidateResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("All candidates", candidateService.getAllCandidates()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CandidateResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Candidate", candidateService.getCandidateById(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CandidateResponse>>> search(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) CandidateStatus status,
            @RequestParam(required = false) String q
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Search results",
                candidateService.searchCandidates(role, status, q)
        ));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<CandidateResponse>> updateStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Status updated",
                candidateService.updateCandidateStatus(id, request.getStatus())
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        candidateService.deleteCandidate(id);
        return ResponseEntity.ok(ApiResponse.success("Candidate deleted"));
    }

    @GetMapping("/{id}/resume")
    public ResponseEntity<byte[]> downloadResume(@PathVariable Long id) {
        byte[] file = candidateService.getCandidateResume(id);
        // Best-effort filename; frontend may override
        String filename = "resume_" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportJson() throws IOException {
        String json = candidateService.exportCandidatesToJson();
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=candidates.json")
                .contentType(MediaType.APPLICATION_JSON)
                .body(bytes);
    }

    @PostMapping(value = "/import", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<CandidateResponse>>> importJson(@RequestBody String json) throws IOException {
        return ResponseEntity.ok(ApiResponse.success(
                "Import completed",
                candidateService.importCandidatesFromJson(json)
        ));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<CandidateStatistics>> stats() {
        return ResponseEntity.ok(ApiResponse.success("Stats", candidateService.getCandidateStatistics()));
    }
}
