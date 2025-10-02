package co.com.backend.reactive.r2dbc.adapter;

import co.com.backend.reactive.model.user.User;
import co.com.backend.reactive.model.user.gateways.UserRepository;
import co.com.backend.reactive.r2dbc.entity.UserEntity;
import co.com.backend.reactive.r2dbc.repository.UserR2dbcRepository;
import co.com.backend.reactive.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    User,
    UserEntity,
    Long,
    UserR2dbcRepository
> implements UserRepository {
    
    public UserReactiveRepositoryAdapter(UserR2dbcRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, User.class));
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(this::toEntity);
    }
}
