package co.com.backend.reactive.usecase.user.exceptions;


public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

}
