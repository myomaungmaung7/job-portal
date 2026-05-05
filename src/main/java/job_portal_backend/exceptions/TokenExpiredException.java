package job_portal_backend.exceptions;

public class TokenExpiredException extends RuntimeException{

    public TokenExpiredException(String message) {
        super(message);
    }
}
