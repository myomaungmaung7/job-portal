package job_portal_backend.mapper;

import job_portal_backend.dto.ProfileRequestDto;
import job_portal_backend.entity.Profile;
import job_portal_backend.entity.User;
import job_portal_backend.entity.enums.Role;

public class ProfileMapper {

    public static Profile toEntity(User user) {

        Profile profile = new Profile();
        profile.setUserId(user.getId());

        if (user.getRole() == Role.EMPLOYER) {
            profile.setBusinessName(user.getUsername());
        }

        return profile;
    }

    public static void updateEntity(Profile profile, ProfileRequestDto dto, Role role) {

        if (role == Role.EMPLOYEE) {
            if (dto.getAddress() != null) profile.setAddress(dto.getAddress());
            if (dto.getSkill() != null) profile.setSkill(dto.getSkill());
            if (dto.getAge() != null) profile.setAge(dto.getAge());
            if (dto.getEducationBackground() != null) profile.setEducationBackground(dto.getEducationBackground());
            if (dto.getExperience() != null) profile.setExperience(dto.getExperience());
        }
        else if (role == Role.EMPLOYER) {
            if (dto.getLocation() != null) profile.setLocation(dto.getLocation());
            if (dto.getBusinessName() != null) profile.setBusinessName(dto.getBusinessName());
            if (dto.getCompanyBackground() != null) profile.setCompanyBackground(dto.getCompanyBackground());
            if (dto.getCrn() != null) profile.setCrn(dto.getCrn());
        }
    }
}
