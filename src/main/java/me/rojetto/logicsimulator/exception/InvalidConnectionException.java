package me.rojetto.logicsimulator.exception;

public class InvalidConnectionException extends RuntimeException {
    public InvalidConnectionException(String msg) {
        super(msg);
    }
}