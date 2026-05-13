package job_portal_backend.service.profile;

import job_portal_backend.dto.ProfileRequestDto;
import job_portal_backend.entity.Profile;
import job_portal_backend.entity.User;
import job_portal_backend.entity.enums.Role;
import job_portal_backend.repository.ProfileRepository;
import job_portal_backend.repository.UserRepository;
import job_portal_backend.response.ApiResponse;
import job_portal_backend.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileServiceImpl implements ProfileService {

    @Value("${app.file.path.profile.absolutePath}")
    private String profileAbs;
    @Value("${app.file.path.profile.relativePath}")
    private String profileRel;

    @Value("${app.file.path.nrcFront.absolutePath}")
    private String nrcFrontAbs;
    @Value("${app.file.path.nrcFront.relativePath}")
    private String nrcFrontRel;

    @Value("${app.file.path.nrcBack.absolutePath}")
    private String nrcBackAbs;
    @Value("${app.file.path.nrcBack.relativePath}")
    private String nrcBackRel;

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final FileUtil fileUtil;


    @Override
    public ApiResponse updateProfile(ProfileRequestDto dto,User loggedInUser) {
          Long userId = loggedInUser.getId();
          Role userRole=loggedInUser.getRole();
        Profile profile = profileRepository.findByUserId(userId)
                .orElse(new Profile());

        try {
            if (dto.getProfileFile() != null && !dto.getProfileFile().isEmpty()) {
                profile.setProfileImage(fileUtil.writeMediaFile(dto.getProfileFile(), profileAbs, profileRel));
            }


            if (userRole == Role.EMPLOYER) {
                profile.setBusinessName(dto.getBusinessName());
                profile.setLocation(dto.getLocation());
                profile.setCompanyBackground(dto.getCompanyBackground());
                profile.setCrn(dto.getCrn());
            }


            else if (userRole == Role.EMPLOYEE) {

                if (dto.getNrcFrontFile() != null && !dto.getNrcFrontFile().isEmpty()) {
                    profile.setNrcFront(fileUtil.writeMediaFile(dto.getNrcFrontFile(), nrcFrontAbs, nrcFrontRel));
                }
                if (dto.getNrcBackFile() != null && !dto.getNrcBackFile().isEmpty()) {
                    profile.setNrcBack(fileUtil.writeMediaFile(dto.getNrcBackFile(), nrcBackAbs, nrcBackRel));
                }

                profile.setSkill(dto.getSkill());
                profile.setAge(dto.getAge());
                profile.setAddress(dto.getAddress());
                profile.setEducationBackground(dto.getEducationBackground());
                profile.setExperience(dto.getExperience());
            }
            Profile savedProfile = profileRepository.save(profile);
            return ApiResponse.builder()
                    .success(1)
                    .code(200)
                    .data(savedProfile)
                    .message("Profile updated successfully")
                    .build();

        } catch (IOException e) {
            throw new RuntimeException("Error during file upload", e);
        }
    }
}
