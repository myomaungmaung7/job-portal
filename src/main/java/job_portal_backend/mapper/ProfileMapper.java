package job_portal_backend.mapper;

import job_portal_backend.entity.Profile;
import job_portal_backend.entity.User;
import job_portal_backend.entity.enums.Role;

public class ProfileMapper {

    public static Profile toEntity(User user) {

        Profile profile = new Profile();
        profile.setUser(user);

        if (user.getRole() == Role.EMPLOYER) {
            profile.setBusinessName(user.getUsername());
        }

        return profile;
    }
}
