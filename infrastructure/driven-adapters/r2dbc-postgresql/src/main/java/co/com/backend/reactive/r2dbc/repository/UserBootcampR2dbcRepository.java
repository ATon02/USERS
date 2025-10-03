package co.com.backend.reactive.r2dbc.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import co.com.backend.reactive.r2dbc.entity.UserBootcampEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserBootcampR2dbcRepository extends ReactiveCrudRepository<UserBootcampEntity, Long>, ReactiveQueryByExampleExecutor<UserBootcampEntity> {

    Flux<UserBootcampEntity> findByUserId(Long userId);
    Mono<Integer> countBootcampsByUserId(Long userId);
}