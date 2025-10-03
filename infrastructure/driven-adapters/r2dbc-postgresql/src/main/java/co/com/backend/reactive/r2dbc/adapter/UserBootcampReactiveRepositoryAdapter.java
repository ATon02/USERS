package co.com.backend.reactive.r2dbc.adapter;

import co.com.backend.reactive.model.userboocamp.UserBootcamp;
import co.com.backend.reactive.model.userboocamp.gateways.UserBootcampRepository;
import co.com.backend.reactive.r2dbc.entity.UserBootcampEntity;
import co.com.backend.reactive.r2dbc.repository.UserBootcampR2dbcRepository;
import co.com.backend.reactive.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class UserBootcampReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    UserBootcamp,
    UserBootcampEntity,
    Long,
    UserBootcampR2dbcRepository
> implements UserBootcampRepository {

    public UserBootcampReactiveRepositoryAdapter(UserBootcampR2dbcRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, UserBootcamp.class));
    }

    @Override
    public Mono<Void> registerUserBootcamp(UserBootcamp userBootcamp) {
        return super.save(userBootcamp).then();
    }

    @Override
    public Flux<UserBootcamp> findByUserId(Long userId) {
        return repository.findByUserId(userId)
                .map(this::toEntity);
    }

    @Override
    public Mono<Integer> countBootcampsByUserId(Long userId) {
        return repository.countBootcampsByUserId(userId);
    }

    
}
