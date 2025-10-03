package co.com.backend.reactive.model.userboocamp.gateways;


import co.com.backend.reactive.model.userboocamp.UserBootcamp;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserBootcampRepository {
    Mono<Void> registerUserBootcamp(UserBootcamp userBootcamp);
    Flux<UserBootcamp> findByUserId(Long userId);
    Mono<Integer> countBootcampsByUserId(Long userId);
}
