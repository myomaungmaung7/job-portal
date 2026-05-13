package job_portal_backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import job_portal_backend.dto.ProfileRequestDto;
import job_portal_backend.entity.User;
import job_portal_backend.response.ApiResponse;
import job_portal_backend.service.profile.ProfileService;
import job_portal_backend.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;


    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('EMPLOYEE', 'EMPLOYER'")
    public ResponseEntity<ApiResponse> update(
            @AuthenticationPrincipal User loggedInUser,
            @ModelAttribute ProfileRequestDto dto,
            HttpServletRequest request)  {

        ApiResponse response = profileService.updateProfile(dto, loggedInUser);
        return ResponseUtils.buildResponse(request, response);
    }
}
