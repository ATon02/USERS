package co.com.backend.reactive.model.user.gateways;

public interface UserRepository {

    Mono<User> save(User user);
}
