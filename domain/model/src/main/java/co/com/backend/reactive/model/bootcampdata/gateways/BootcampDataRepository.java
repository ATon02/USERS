package co.com.backend.reactive.model.bootcampdata.gateways;

import co.com.backend.reactive.model.bootcampdata.BootcampData;
import reactor.core.publisher.Mono;

public interface BootcampDataRepository {
    Mono<BootcampData> findById(Long id);
    Mono<Boolean> existsById(Long id);
}
