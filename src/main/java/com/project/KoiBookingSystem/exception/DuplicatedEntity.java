package com.project.KoiBookingSystem.exception;

public class DuplicatedEntity extends RuntimeException{

    public DuplicatedEntity(String message) {
        super(message);
    }
}
