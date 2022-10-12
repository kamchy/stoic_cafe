package com.kamilachyla.stoic.config;

// TODO hanlde exceptions in REST-ful way

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.xml.bind.ValidationException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler({ValidationException.class})
        public ResponseEntity<ErrorResponse> validationException(
                ValidationException ex,
                HttpServletRequest request){

            return new ResponseEntity<>(
                    new ErrorResponse("Validation failed", ex.getLocalizedMessage(), LocalDateTime.now()),
                    HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler({Exception.class})
        public ResponseEntity<ErrorResponse> genericException(
                Exception ex,
                HttpServletRequest request) {
            return new ResponseEntity<>(
                    new ErrorResponse("Exception handler", ex.getLocalizedMessage(), LocalDateTime.now()),
                    HttpStatus.BAD_REQUEST);
        }

        public record ErrorResponse(String comment, String message, LocalDateTime date) {
    }
}
