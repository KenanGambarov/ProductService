package com.productservice.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ExceptionResponse {

    private LocalDateTime timestamp;
    private String message;
    private int status;
    private String errors;
    private Map<String, Object> details;

}
