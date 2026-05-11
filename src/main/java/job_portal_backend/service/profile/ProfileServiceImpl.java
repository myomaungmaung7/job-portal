package job_portal_backend.service.profile;

import job_portal_backend.dto.ProfileRequestDto;
import job_portal_backend.entity.Profile;
import job_portal_backend.entity.User;
import job_portal_backend.mapper.ProfileMapper;
import job_portal_backend.repository.ProfileRepository;
import job_portal_backend.repository.UserRepository;
import job_portal_backend.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Override
    public ApiResponse updateProfile(User user, ProfileRequestDto dto, String img, String nrcF, String nrcB) {

        user.setUserName(dto.getUserName()); // Use whatever field name you have in User.java
        user.setPhoneNumber(dto.getPhoneNumber());

        Profile profile = profileRepository.findByUserId(user.getId()).orElse(new Profile());
        profile.setUserId(user.getId());


        ProfileMapper.updateEntity(profile, dto, user.getRole());

        if (img != null) profile.setProfileImage(img);
        if (nrcF != null) profile.setNrcFront(nrcF);
        if (nrcB != null) profile.setNrcBack(nrcB);

        userRepository.save(user);
        profileRepository.save(profile);
        return ApiResponse.builder().success(1).code(200).message("Profile updated").build();
    }
}
