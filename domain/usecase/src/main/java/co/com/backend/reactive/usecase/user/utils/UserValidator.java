package co.com.backend.reactive.usecase.user.utils;

import co.com.backend.reactive.model.user.User;
import co.com.backend.reactive.usecase.user.enums.UserError;
import reactor.core.publisher.Mono;
import java.util.regex.Pattern;

public class UserValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    public static Mono<User> validate(User user) {
        return Mono.fromCallable(() -> {
            validateBasicFields(user);
            return user;
        });
    }

    public static Mono<User> validateForSave(User user) {
        return validate(user);
    }

    public static Mono<User> validateForUpdate(User user) {
        return Mono.fromCallable(() -> {
            if (user.getId() == null || user.getId() <= 0) {
                throw new IllegalArgumentException(UserError.USER_ID_REQUIRED.getMessage());
            }
            validateBasicFields(user);
            return user;
        });
    }

    private static void validateBasicFields(User user) {
        if (user == null) {
            throw new IllegalArgumentException(UserError.USER_NULL.getMessage());
        }

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException(UserError.USER_NAME_REQUIRED.getMessage());
        }

        if (user.getName().trim().length() < 2) {
            throw new IllegalArgumentException(UserError.USER_NAME_TOO_SHORT.getMessage());
        }

        if (user.getName().trim().length() > 100) {
            throw new IllegalArgumentException(UserError.USER_NAME_TOO_LONG.getMessage());
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException(UserError.USER_EMAIL_REQUIRED.getMessage());
        }

        if (!EMAIL_PATTERN.matcher(user.getEmail().trim()).matches()) {
            throw new IllegalArgumentException(UserError.USER_EMAIL_INVALID_FORMAT.getMessage());
        }

        if (user.getEmail().trim().length() > 255) {
            throw new IllegalArgumentException(UserError.USER_EMAIL_TOO_LONG.getMessage());
        }
    }
}
