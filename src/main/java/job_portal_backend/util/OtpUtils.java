package job_portal_backend.util;

public class OtpUtils {

    public static String generateOtp() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}
