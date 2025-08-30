package edu.centraluniversity.app.controller;

import com.google.api.Http;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import edu.centraluniversity.app.model.exception.ErrorDto;
import edu.centraluniversity.app.model.exception.HttpStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class CommonExceptionController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationError(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDto(HttpStatus.BAD_REQUEST.name(), message));
    }

    @ExceptionHandler(HttpStatusException.class)
    public ResponseEntity<ErrorDto> handleHttpStatusException(HttpStatusException e) {
        return ResponseEntity.status(e.getStatus())
                .body(new ErrorDto(e.getStatus().toString(), e.getMessage()));
    }

    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<ErrorDto> handleStatusRuntimeException(StatusRuntimeException e) {

        if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorDto(e.getStatus().getCode().name(), e.getStatus().getDescription()));
        }

        if (e.getStatus().getCode() == Status.Code.INVALID_ARGUMENT || e.getStatus().getCode() == Status.Code.CANCELLED) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorDto(HttpStatus.BAD_REQUEST.name(), e.getStatus().getDescription()));
        }

        if (e.getStatus().getCode() == Status.Code.ALREADY_EXISTS) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorDto(e.getStatus().getCode().name(), e.getStatus().getDescription()));
        }

        if (e.getStatus().getCode() == Status.Code.INTERNAL) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDto("INTERNAL_AUTH_SERVICE_ERROR", e.getStatus().getDescription()));
        }

        if (e.getStatus().getCode() == Status.Code.UNAVAILABLE) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ErrorDto(e.getStatus().getCode().name(), "Auth service unavailable"));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto(e.getStatus().getCode().name(), e.getStatus().getDescription()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto("500 INTERNAL_SERVER_ERROR", e.getMessage()));
    }
}
