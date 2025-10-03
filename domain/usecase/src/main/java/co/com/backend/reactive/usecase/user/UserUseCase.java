package co.com.backend.reactive.usecase.user;

import java.util.List;

import co.com.backend.reactive.model.bootcampdata.gateways.BootcampDataRepository;
import co.com.backend.reactive.model.user.User;
import co.com.backend.reactive.model.user.gateways.UserRepository;
import co.com.backend.reactive.model.userboocamp.UserBootcamp;
import co.com.backend.reactive.model.userboocamp.gateways.UserBootcampRepository;
import co.com.backend.reactive.usecase.user.utils.UserValidator;
import co.com.backend.reactive.usecase.user.enums.UserError;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase implements IUserUseCase {
    
    private final UserRepository userRepository;
    private final UserBootcampRepository userBootcampRepository;
    private final BootcampDataRepository bootcampDataRepository;
    
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

    @Override
    public Mono<Void> registerUserBootcamp(Long userId, List<Long> bootcampIds) {
        if (userId == null) {
            return Mono.error(new IllegalArgumentException(UserError.USER_ID_REQUIRED.getMessage()));
        }
        
        if (bootcampIds == null || bootcampIds.isEmpty()) {
            return Mono.error(new IllegalArgumentException(UserError.BOOTCAMP_LIST_EMPTY.getMessage()));
        }
        
        if (bootcampIds.size() > 5) {
            return Mono.error(new IllegalArgumentException(UserError.BOOTCAMP_LIST_TOO_LARGE.getMessage()));
        }
        
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException(UserError.USER_NOT_FOUND.getMessage())))
            .flatMap(user -> 
                Flux.fromIterable(bootcampIds)
                    .flatMap(bootcampId -> 
                        bootcampDataRepository.findById(bootcampId)
                            .switchIfEmpty(Mono.error(new IllegalArgumentException(
                                UserError.BOOTCAMP_NOT_FOUND.getMessage() + ": " + bootcampId)))
                    )
                    .then(
                        userBootcampRepository.findByUserId(userId)
                            .map(userBootcamp -> userBootcamp.getBootcampId())
                            .collectList()
                            .flatMap(registeredBootcampIds -> {
                                boolean hasConflict = bootcampIds.stream()
                                    .anyMatch(registeredBootcampIds::contains);
                                if (hasConflict) {
                                    return Mono.<Void>error(new IllegalArgumentException(UserError.USER_ALREADY_HAS_BOOTCAMPS.getMessage()));
                                }
                                return Flux.fromIterable(bootcampIds)
                                    .map(bootcampId -> UserBootcamp.builder()
                                        .userId(userId)
                                        .bootcampId(bootcampId)
                                        .build())
                                    .flatMap(userBootcampRepository::registerUserBootcamp)
                                    .then();
                            })
                    )
            );
    }
}
