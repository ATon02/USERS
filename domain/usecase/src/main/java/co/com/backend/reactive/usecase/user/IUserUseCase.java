package co.com.backend.reactive.usecase.user;

import co.com.backend.reactive.model.user.User;
import reactor.core.publisher.Mono;

public interface IUserUseCase {
    Mono<User> save(User user);

}
