package job_portal_backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import job_portal_backend.dto.JobRequestDto;
import job_portal_backend.entity.User;
import job_portal_backend.response.ApiResponse;
import job_portal_backend.service.job.JobService;
import job_portal_backend.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping("/post")

    public ResponseEntity <ApiResponse> postNewJob(@AuthenticationPrincipal User user, @RequestBody JobRequestDto jobRequestDto, HttpServletRequest httpRequest) {

        ApiResponse response=jobService.postNewJob(user, jobRequestDto);
        return ResponseUtils.buildResponse(httpRequest, response);
    }

    @PutMapping("/update/{id}")

    public ResponseEntity<ApiResponse> updateJob(
            @PathVariable("id") Long jobId,
            @AuthenticationPrincipal User user,
            @RequestBody JobRequestDto jobRequestDto,
            HttpServletRequest httpRequest) {

        ApiResponse response = jobService.updateJob(jobId, user, jobRequestDto);
        return ResponseUtils.buildResponse(httpRequest, response);
    }

}
