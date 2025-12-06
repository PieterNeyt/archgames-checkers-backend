package be.kdg.ip3.checkersbackend.domain;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
