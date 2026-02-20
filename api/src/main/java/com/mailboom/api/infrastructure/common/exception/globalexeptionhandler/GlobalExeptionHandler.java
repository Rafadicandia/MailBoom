package com.mailboom.api.infrastructure.common.exception.globalexeptionhandler;

import com.mailboom.api.application.common.exception.*;
import com.mailboom.api.domain.exception.*;
import com.mailboom.api.infrastructure.common.dto.ErrorResponse;
import com.mailboom.api.infrastructure.common.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExeptionHandler {

    // --- Authentication Exceptions (401) ---

    @ExceptionHandler({
            BadCredentialsException.class,
            AuthenticationException.class
    })
    public ResponseEntity<ErrorResponse> handleAuthenticationExceptions(RuntimeException ex) {
        return buildErrorResponse("Invalid credentials", HttpStatus.UNAUTHORIZED);
    }

    // --- Not Found Exceptions (404) ---

    @ExceptionHandler({
            UserNotFoundException.class,
            EmailNotFoundException.class,
            ContactNotFoundException.class,
            CampaignNotFoundException.class,
            ContactListIdNotFoundException.class,
            UserIdNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(RuntimeException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // --- Bad Request Exceptions (400) ---

    @ExceptionHandler({
            ContactListAlreadyExistException.class,
            UserWithEmailAlreadyExistsException.class,
            SubjectFormatException.class,
            PasswordCannotBeNullException.class,
            ContactListMustHaveNameException.class,
            ContactListIdCannotBeNullException.class,
            EmailCannotBeNullException.class,
            CampaignIdCannotBeNullException.class,
            InvalidEmailException.class,
            UserCannotBeNullException.class,
            ContactListSizeExceedsLimitException.class,
            HtmlContentExtensionException.class,
            ContactCannotBeNullException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(RuntimeException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // --- Forbidden Exceptions (403) ---

    @ExceptionHandler({
            CampaignIsNotFromUserException.class,
            UserIsNotAdmiException.class
    })
    public ResponseEntity<ErrorResponse> handleForbiddenExceptions(RuntimeException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    // --- Internal Server Error Exceptions (500) ---

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<ErrorResponse> handleEmailSendingException(EmailSendingException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // --- Global Exception Handler ---

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        return buildErrorResponse("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status) {
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}
