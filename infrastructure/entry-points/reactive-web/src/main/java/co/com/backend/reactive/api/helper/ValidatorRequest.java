package co.com.backend.reactive.api.helper;

import co.com.backend.reactive.api.dtos.request.RegisterBootcampRequest;
import co.com.backend.reactive.api.dtos.request.UserRequestDTO;
import co.com.backend.reactive.api.exceptions.BusinessExceptionHandler;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ValidatorRequest {

    private final Validator validator;

    public void validateDTOUser(UserRequestDTO dto) {
        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new BusinessExceptionHandler(violations);
        }
    }

    public void validateDTORegisterBootcamp(RegisterBootcampRequest dto) {
        Set<ConstraintViolation<RegisterBootcampRequest>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new BusinessExceptionHandler(violations);
        }
    }
}
