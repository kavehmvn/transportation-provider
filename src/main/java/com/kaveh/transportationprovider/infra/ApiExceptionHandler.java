package com.kaveh.transportationprovider.infra;

import com.google.gson.JsonObject;
import com.kaveh.transportationprovider.model.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(ApiException.class)
    public ResponseEntity handleException(ApiException e) {
        logger.error("Exception occurred during operation:", e);
        // log exception
        JsonObject error = new JsonObject();
        error.addProperty("message", e.getMessage());
        return ResponseEntity.status(e.getHttpStatus()).body(error.toString());
    }
}