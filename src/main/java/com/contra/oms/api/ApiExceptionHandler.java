package com.contra.oms.api;

import com.contra.oms.domain.InvalidOrderTransitionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(InvalidOrderTransitionException.class)
    public ResponseEntity<ApiModels.ErrorView> invalidTransition(InvalidOrderTransitionException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiModels.ErrorView("invalid_transition", ex.getMessage()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiModels.ErrorView> requestError(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(new ApiModels.ErrorView("request_error", ex.getReason()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiModels.ErrorView> stateError(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiModels.ErrorView("operation_conflict", ex.getMessage()));
    }
}
