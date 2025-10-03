package co.com.backend.reactive.bootcampintercom;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Component;
import co.com.backend.reactive.model.bootcampdata.BootcampData;
import co.com.backend.reactive.model.bootcampdata.gateways.BootcampDataRepository;
import co.com.backend.reactive.bootcampintercom.dto.BootcampIntercomResponse;


@Component
public class BootcampIntercomAdapter implements BootcampDataRepository {

     private final WebClient webClient;

    public BootcampIntercomAdapter(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8082").build();
    }

    @Override
    public Mono<BootcampData> findById(Long id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/bootcamp/{id}")
                                             .build(id))
                .retrieve()
                .bodyToMono(BootcampIntercomResponse.class)
                .filter(response -> response.getStatus() == 200 && response.getData() != null)
                .map(BootcampIntercomResponse::getData)
                .onErrorResume(e -> {
                    return Mono.empty();
                });
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        return findById(id)
                .map(capacity -> true)
                .defaultIfEmpty(false);
    }
}
