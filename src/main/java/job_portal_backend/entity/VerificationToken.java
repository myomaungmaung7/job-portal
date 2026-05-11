package job_portal_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_token")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VerificationToken extends BaseEntity {

    @NotBlank
    @Column(name = "otp", length = 6)
    private String otp;

    @NotNull
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @NotNull
    @Column(name = "user_id", unique = true)
    private Long userId;

    public VerificationToken(String otp, Long userId, int expiryMinutes) {
        this.otp = otp;
        this.userId = userId;
        this.expiryDate = LocalDateTime.now().plusMinutes(expiryMinutes);
    }
}
