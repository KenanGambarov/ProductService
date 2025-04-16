package com.productservice.exception;

import lombok.Getter;

@Getter
public enum ExceptionConstants {

    NOT_FOUND("Not Found"),
    VALIDATION_FAILED("Validation Failed"),
    INTERNAL_SERVER_ERROR("Internal Server Error"),
    UNEXPECTED_ERROR("Unexpected Error"),
    CLIENT_ERROR("Exception from Client"),
    PRODUCT_NOT_FOUND("Product Not Found"),
    PRODUCT_CATEGORY_NOT_FOUND("Product category not found");

    private final String message;

    ExceptionConstants(String message) {
        this.message = message;
    }

}
