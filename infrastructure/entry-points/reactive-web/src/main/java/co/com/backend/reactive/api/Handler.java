package co.com.backend.reactive.api;

import co.com.backend.reactive.api.dtos.request.UserRequestDTO;
import co.com.backend.reactive.api.dtos.response.BaseResponse;
import co.com.backend.reactive.api.mapper.UserDTOMapper;
import co.com.backend.reactive.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class Handler {
    
    private final UserUseCase userUseCase;
    private final UserDTOMapper userDTOMapper;

    public Mono<ServerResponse> saveUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRequestDTO.class)
                .map(userDTOMapper::toModel)
                .flatMap(userUseCase::save)
                .map(userDTOMapper::toResponseDTO)
                .flatMap(userResponse -> {
                    BaseResponse<Object> response = BaseResponse.builder()
                            .status(200)
                            .message("User created successfully")
                            .path(serverRequest.path())
                            .timestamp(LocalDateTime.now())
                            .data(userResponse)
                            .build();
                    return ServerResponse.ok().bodyValue(response);
                });
    }
}
