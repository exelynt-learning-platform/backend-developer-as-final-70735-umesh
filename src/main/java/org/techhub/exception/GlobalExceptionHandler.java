package org.techhub.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.techhub.dto.ValidationErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // =====================================================
    // STEP 66 - RESOURCE NOT FOUND
    // HTTP 404
    // =====================================================

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ValidationErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex) {

        ValidationErrorResponse response =
                new ValidationErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        "Resource Not Found",
                        ex.getMessage()
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.NOT_FOUND
        );
    }

    // =====================================================
    // STEP 67 - RESERVATION NOT FOUND
    // HTTP 404
    // =====================================================

    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<ValidationErrorResponse> handleReservationNotFound(
            ReservationNotFoundException ex) {

        ValidationErrorResponse response =
                new ValidationErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        "Reservation Not Found",
                        ex.getMessage()
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.NOT_FOUND
        );
    }

    // =====================================================
    // STEP 68 - USER NOT FOUND
    // HTTP 404
    // =====================================================

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ValidationErrorResponse> handleUserNotFound(
            UserNotFoundException ex) {

        ValidationErrorResponse response =
                new ValidationErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        "User Not Found",
                        ex.getMessage()
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.NOT_FOUND
        );
    }

    // =====================================================
    // STEP 69 - VALIDATION ERROR
    // HTTP 400
    // =====================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                        error.getField()
                                + ": "
                                + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ValidationErrorResponse response =
                new ValidationErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation Failed",
                        message
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    // =====================================================
    // ACCESS DENIED EXCEPTION
    // HTTP 403
    // =====================================================

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ValidationErrorResponse> handleAccessDenied(
            AccessDeniedException ex) {

        ValidationErrorResponse response =
                new ValidationErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.FORBIDDEN.value(),
                        "Forbidden",
                        ex.getMessage() != null ? ex.getMessage() : "Access denied"
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.FORBIDDEN
        );
    }

    // =====================================================
    // AUTHENTICATION EXCEPTION
    // HTTP 401
    // =====================================================

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ValidationErrorResponse> handleAuthenticationException(
            AuthenticationException ex) {

        ValidationErrorResponse response =
                new ValidationErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.UNAUTHORIZED.value(),
                        "Unauthorized",
                        ex.getMessage() != null ? ex.getMessage() : "Authentication failed"
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.UNAUTHORIZED
        );
    }

    // =====================================================
    // STEP 70 - BAD REQUEST / ILLEGAL ARGUMENT
    // HTTP 400
    // =====================================================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ValidationErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex) {

        ValidationErrorResponse response =
                new ValidationErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad Request",
                        ex.getMessage()
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    // =====================================================
    // ILLEGAL STATE EXCEPTION
    // HTTP 400
    // =====================================================

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ValidationErrorResponse> handleIllegalStateException(
            IllegalStateException ex) {

        ValidationErrorResponse response =
                new ValidationErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        "Invalid Operation",
                        ex.getMessage()
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    // =====================================================
    // DATA INTEGRITY VIOLATION EXCEPTION
    // HTTP 409 / 400
    // =====================================================

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleDataIntegrityViolation(
            org.springframework.dao.DataIntegrityViolationException ex) {

        ValidationErrorResponse response =
                new ValidationErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.CONFLICT.value(),
                        "Conflict",
                        "Database constraint violation: The requested entity is referenced by other records or violates unique constraints."
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.CONFLICT
        );
    }

    // =====================================================
    // RUNTIME EXCEPTION
    // HTTP 400
    // =====================================================

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ValidationErrorResponse> handleRuntimeException(
            RuntimeException ex) {

        ValidationErrorResponse response =
                new ValidationErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad Request",
                        ex.getMessage()
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    // =====================================================
    // INTERNAL SERVER ERROR
    // HTTP 500
    // =====================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ValidationErrorResponse> handleGeneralException(
            Exception ex) {

        ValidationErrorResponse response =
                new ValidationErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal Server Error",
                        "Something went wrong"
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}