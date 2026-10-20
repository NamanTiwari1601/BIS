package com.proto.BIS.common.Exception;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void mapsBusinessAndNotFoundErrors() {
        var bad = handler.handleIllegalArgument(new IllegalArgumentException("bad request"));
        assertEquals(400, bad.getStatusCode().value());
        assertEquals("bad request", bad.getBody().get("message"));

        var missing = handler.handleEntityNotFound(new EntityNotFoundException("missing"));
        assertEquals(404, missing.getStatusCode().value());
        assertEquals("missing", missing.getBody().get("message"));
    }

    @Test
    void mapsUnexpectedErrorsToInternalServerError() {
        var response = handler.handleGenericException(new RuntimeException("boom"));
        assertEquals(500, response.getStatusCode().value());
        assertEquals("Something went wrong. Please try again.", response.getBody().get("message"));
    }
}