package co.com.backend.reactive.usecase.user;

import co.com.backend.reactive.model.user.User;
import co.com.backend.reactive.model.user.gateways.UserRepository;
import co.com.backend.reactive.usecase.user.utils.UserValidator;
import co.com.backend.reactive.usecase.user.enums.UserError;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase implements IUserUseCase {
    
    private final UserRepository userRepository;
    
    @Override
    public Mono<User> save(User user) {
        return UserValidator.validateForSave(user)
            .flatMap(validUser -> 
                userRepository.findByEmail(validUser.getEmail())
                    .flatMap(exist -> Mono.<User>error(
                        new IllegalArgumentException(UserError.USER_EMAIL_ALREADY_EXISTS.getMessage()))
                    )
                    .switchIfEmpty(
                        Mono.defer(() -> 
                            userRepository.save(validUser)
                                .switchIfEmpty(Mono.error(new IllegalArgumentException(UserError.USER_NOT_CREATED.getMessage())))
                        )
                    )
            );
    }
}
