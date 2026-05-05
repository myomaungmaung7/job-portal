package job_portal_backend.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import job_portal_backend.response.ApiResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), "Invalid argument provided.", request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse> handleEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), "Entity not found.", request);
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<ApiResponse> handleDuplicateEntityException(DuplicateEntityException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), "Duplicate entity detected.", request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        StringBuilder violationMessages = new StringBuilder();

        ex.getConstraintViolations().forEach(violation -> {
            violationMessages.append(violation.getPropertyPath().toString())
                    .append(": ")
                    .append(violation.getMessage())
                    .append("; ");
        });

        if (violationMessages.isEmpty()) {
            violationMessages.append("Validation failed with no specific violations.");
        }

        return buildErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Validation failed",
                violationMessages.toString(),
                request
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @NotNull HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        List<Map<String, String>> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("field", error.getField());
            errorMap.put("message", error.getDefaultMessage());
            errors.add(errorMap);
        });

        HttpServletRequest httpServletRequest = ((HttpServletRequest) request.resolveReference(WebRequest.REFERENCE_REQUEST));
        assert httpServletRequest != null;
        ApiResponse errorResponse = ApiResponse.builder()
                .success(0)
                .code(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .message("Validation failed")
                .data(errors)
                .meta(Map.of(
                        "method", httpServletRequest.getMethod(),
                        "endpoint", httpServletRequest.getRequestURI()
                ))
                .build();

        return new ResponseEntity<>(errorResponse, headers, status);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse> handleUnauthorizedException(UnauthorizedException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), "Unauthorized", request);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse> handleTokenExpiredException(TokenExpiredException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.GONE, ex.getMessage(), "Token Expired", request);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse> handleExpiredJwtException(ExpiredJwtException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.GONE, ex.getMessage(), "Token Expired", request);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse> handleJwtException(JwtException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.GONE, ex.getMessage(), "Token Expired", request);
    }

    @ExceptionHandler(EntityDeletionException.class)
    public ResponseEntity<ApiResponse> handleEntityDeletionException(EntityDeletionException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), "Entity Deletion", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGlobalException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), "An unexpected error occurred.", request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  @NotNull HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        HttpServletRequest httpServletRequest = ((HttpServletRequest) request.resolveReference(WebRequest.REFERENCE_REQUEST));
        assert httpServletRequest != null;

        ApiResponse errorResponse = ApiResponse.builder()
                .success(0)
                .code(HttpStatus.BAD_REQUEST.value())
                .message("Malformed JSON request")
                .data(ex.getLocalizedMessage())
                .meta(Map.of(
                        "method", httpServletRequest.getMethod(),
                        "endpoint", httpServletRequest.getRequestURI()
                ))
                .build();

        return new ResponseEntity<>(errorResponse, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                         @NotNull HttpHeaders headers,
                                                                         HttpStatusCode status,
                                                                         WebRequest request) {
        HttpServletRequest httpServletRequest = ((HttpServletRequest) request.resolveReference(WebRequest.REFERENCE_REQUEST));
        assert httpServletRequest != null;

        ApiResponse errorResponse = ApiResponse.builder()
                .success(0)
                .code(HttpStatus.METHOD_NOT_ALLOWED.value())
                .message("HTTP method not allowed")
                .data(ex.getMessage())
                .meta(Map.of(
                        "method", httpServletRequest.getMethod(),
                        "endpoint", httpServletRequest.getRequestURI()
                ))
                .build();

        return new ResponseEntity<>(errorResponse, headers, status);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), "Access is denied", request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          @NotNull HttpHeaders headers,
                                                                          HttpStatusCode status,
                                                                          WebRequest request) {
        HttpServletRequest httpServletRequest = ((HttpServletRequest) request.resolveReference(WebRequest.REFERENCE_REQUEST));
        assert httpServletRequest != null;

        ApiResponse errorResponse = ApiResponse.builder()
                .success(0)
                .code(HttpStatus.BAD_REQUEST.value())
                .message("Required request parameter is missing")
                .data(ex.getMessage())
                .meta(Map.of(
                        "method", httpServletRequest.getMethod(),
                        "endpoint", httpServletRequest.getRequestURI()
                ))
                .build();

        return new ResponseEntity<>(errorResponse, headers, status);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse> handleDataAccessException(DataAccessException ex, HttpServletRequest request) {
        return buildErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Database access error",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ApiResponse> handleTransactionSystemException(TransactionSystemException ex, HttpServletRequest request) {
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Transaction failed",
                ex.getMessage(),
                request
        );
    }


    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<ApiResponse> handlePersistenceException(PersistenceException ex, HttpServletRequest request) {
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Persistence error",
                ex.getMessage(),
                request
        );
    }


    private ResponseEntity<ApiResponse> buildErrorResponse(HttpStatus status, String message, String details, HttpServletRequest request) {
        ApiResponse errorResponse = ApiResponse.builder()
                .success(0)
                .code(status.value())
                .message(message)
                .data(details)
                .meta(Map.of(
                        "method", request.getMethod(),
                        "endpoint", request.getRequestURI()
                ))
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }
}
