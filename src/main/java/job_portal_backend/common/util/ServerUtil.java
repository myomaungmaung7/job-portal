package job_portal_backend.common.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import job_portal_backend.entity.User;
import job_portal_backend.entity.VerificationToken;
import job_portal_backend.repository.VerificationTokenRepository;
import job_portal_backend.service.verificationMail.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ServerUtil {

    private final VerificationTokenRepository tokenRepository;
    private final JavaMailSender javaMailSender;
    private final EmailService emailService;

    @Value("${spring.mail.username}")
    private String fromMail;

    @Value("${jwt.secret}")
    private String secretKey;

    private final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 10;

    public String getSecretKey() { return secretKey; }

    // Generate OTP
    public String generateNumericCode(int length) {
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    // Send Code and Save to DB
    @Transactional
    public void sendCodeToEmail(User user, int expirationMinutes, String templateName) {
        String otp = generateNumericCode(6);

        tokenRepository.deleteByUser(user);

        VerificationToken token = new VerificationToken(otp, user, expirationMinutes);
        tokenRepository.save(token);

        try {
            sendEmail(user.getEmail(), otp, templateName);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }

    public String loadTemplate(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(FileCopyUtils.copyToByteArray(inputStream), StandardCharsets.UTF_8);
        }
    }

    // Send Email (HTML)
    private void sendEmail(String email, String otp, String templateName) throws MessagingException, IOException {
        String userName = email.split("@")[0];
        String htmlTemplate = loadTemplate("templates/" + templateName + ".html");
        String htmlContent = htmlTemplate
                .replace("{{username}}", userName)
                .replace("{{code}}", otp);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setFrom(fromMail);
        helper.setSubject("Verification Code");
        helper.setText(htmlContent, true);

        this.emailService.sendEmail(email, "Verification Code", htmlContent);
    }

    public String generateToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();

        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put("role", roles);
//        SecretKey key = Keys.hmacShaKeyFor(SecretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        long refresh_Token_ExpireTime = 1000 * 60 * 720;
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put("role", roles);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+ refresh_Token_ExpireTime))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }
}
