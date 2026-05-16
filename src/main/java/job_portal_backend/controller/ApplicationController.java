package job_portal_backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import job_portal_backend.entity.User;
import job_portal_backend.response.ApiResponse;
import job_portal_backend.service.application.ApplicationService;
import job_portal_backend.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applyService;

    @PostMapping(value = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<ApiResponse> applyJob(
            @AuthenticationPrincipal User loggedInUser,
            @RequestParam("jobId") Long jobId,
            @RequestParam("cvFile")MultipartFile cvFile,
            HttpServletRequest httpRequest
            ) {

        ApiResponse response = applyService.applyJob(jobId, cvFile, loggedInUser);
        return ResponseUtils.buildResponse(httpRequest, response);
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasAuthority('EMPLOYER')")
    public ResponseEntity<ApiResponse> getJobApplications(
            @PathVariable("jobId") Long jobId,
            @AuthenticationPrincipal User loggedInUser,
            HttpServletRequest httpRequest) {

        ApiResponse response = applyService.getApplicationsForJob(jobId, loggedInUser);
        return ResponseUtils.buildResponse(httpRequest, response);
    }

    @GetMapping("/download-cv/{applicationId}")
    @PreAuthorize("hasAuthority('EMPLOYER')")
    public ResponseEntity<?> downloadCv(
            @PathVariable("applicationId") Long applicationId,
            @AuthenticationPrincipal User loggedInUser) {

        try {

            Resource fileResource = applyService.downloadCvFile(applicationId, loggedInUser);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                    .body(fileResource);

        } catch (RuntimeException ex) {

            return ResponseEntity.status(400)
                    .body(ApiResponse.builder()
                            .success(0)
                            .code(400)
                            .message(ex.getMessage())
                            .build());
        }
    }
}
