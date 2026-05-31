package job_portal_backend.mapper;
import job_portal_backend.entity.Profile;
import job_portal_backend.entity.User;
import job_portal_backend.entity.enums.Role;

public class ProfileMapper {

    public static Profile toEntity(User user) {

        Profile profile = new Profile();
        profile.setUserId(user.getId());


        if (user.getRole() == Role.EMPLOYER) {
            profile.setBusinessName(user.getUsername());
        }else{
            profile.setEmployeeName(user.getUsername());
        }

        return profile;
    }
}
