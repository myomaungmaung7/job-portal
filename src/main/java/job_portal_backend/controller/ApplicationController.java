package job_portal_backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import job_portal_backend.entity.User;
import job_portal_backend.response.ApiResponse;
import job_portal_backend.service.application.ApplicationService;
import job_portal_backend.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
}
