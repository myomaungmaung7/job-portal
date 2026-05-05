package job_portal_backend.exceptions;

public class DuplicateEntityException extends RuntimeException{

    public DuplicateEntityException(String message) {
        super(message);
    }
}
