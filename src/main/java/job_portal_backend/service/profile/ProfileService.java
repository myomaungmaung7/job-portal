package job_portal_backend.service.profile;

import job_portal_backend.dto.ProfileRequestDto;
import job_portal_backend.entity.User;
import job_portal_backend.response.ApiResponse;

public interface ProfileService {
    ApiResponse updateProfile(ProfileRequestDto dto,User loggedInUser);
}
