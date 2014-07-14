package net.year4000.mapnodes.exceptions;

public class InvalidJsonException extends IllegalArgumentException {
    public InvalidJsonException(String message) {
        super(message);
    }
}
