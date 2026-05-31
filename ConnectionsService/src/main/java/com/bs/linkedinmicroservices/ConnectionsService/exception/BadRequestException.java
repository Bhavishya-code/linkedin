package com.bs.linkedinmicroservices.ConnectionsService.exception;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message) {
        super(message);
    }
}
