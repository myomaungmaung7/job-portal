package job_portal_backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import job_portal_backend.dto.ProfileRequestDto;
import job_portal_backend.entity.User;
import job_portal_backend.response.ApiResponse;
import job_portal_backend.service.file.FileStorageService;
import job_portal_backend.service.profile.ProfileService;
import job_portal_backend.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final FileStorageService fileStorageService;

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> update(
            @AuthenticationPrincipal User user,
            @RequestPart("data") String jsonData,
            @RequestPart(value = "profileImage", required = false) MultipartFile pImg,
            @RequestPart(value = "nrcFront", required = false) MultipartFile nF,
            @RequestPart(value = "nrcBack", required = false) MultipartFile nB,
            HttpServletRequest request) throws IOException {

        // 1. Convert JSON string to DTO
        ProfileRequestDto dto = new ObjectMapper().readValue(jsonData, ProfileRequestDto.class);

        // 2. Save physical files
        String imgPath = fileStorageService.saveFile(pImg, "profile");
        String nrcFPath = fileStorageService.saveFile(nF, "nrcF");
        String nrcBPath = fileStorageService.saveFile(nB, "nrcB");

        // 3. Update Database
        ApiResponse response = profileService.updateProfile(user, dto, imgPath, nrcFPath, nrcBPath);
        return ResponseUtils.buildResponse(request, response);
    }
}
