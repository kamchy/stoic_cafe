package com.kamilachyla.stoic.config;

// TODO hanlde exceptions in REST-ful way

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.xml.bind.ValidationException;
import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

        @ExceptionHandler(ValidationException.class)
        public ProblemDetail validationException(
                ValidationException ex,
                HttpServletRequest request){
            return detailFromException(ex, request, "Validation exception");
        }

        @ExceptionHandler
        public ProblemDetail genericException(
                Exception ex,
                HttpServletRequest request) {
            return detailFromException(ex, request, "Exception");

        }

    private static ProblemDetail detailFromException(Exception ex, HttpServletRequest request, String title) {
        var message = ex.getMessage();
        var result = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        result.setDetail(message);
        result.setInstance(URI.create(request.getRequestURI()));
        result.setTitle(title);
        return result;
    }
}
