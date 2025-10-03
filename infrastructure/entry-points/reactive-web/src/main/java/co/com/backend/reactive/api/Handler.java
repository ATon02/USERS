package co.com.backend.reactive.api;

import co.com.backend.reactive.api.dtos.request.RegisterBootcampRequest;
import co.com.backend.reactive.api.dtos.request.UserRequestDTO;
import co.com.backend.reactive.api.dtos.response.BaseResponse;
import co.com.backend.reactive.api.mapper.UserDTOMapper;
import co.com.backend.reactive.usecase.user.IUserUseCase;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class Handler {
    
    private final IUserUseCase userUseCase;
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

    public Mono<ServerResponse> registerUserBootcamp(ServerRequest request) {
        return Mono.just(request.pathVariable("userId"))
                .flatMap(userIdStr -> {
                    try {
                        return Mono.just(Long.parseLong(userIdStr));
                    } catch (NumberFormatException e) {
                        return Mono.error(new IllegalArgumentException("Invalid user ID format"));
                    }
                })
                .zipWith(request.bodyToMono(RegisterBootcampRequest.class))
                .flatMap(tuple -> {
                    Long userId = tuple.getT1();
                    RegisterBootcampRequest bootcampRequest = tuple.getT2();
                    return userUseCase.registerUserBootcamp(userId, bootcampRequest.getBootcampIds());
                })
                .then(Mono.defer(() -> {
                    BaseResponse<Void> response = BaseResponse.<Void>builder()
                            .status(HttpStatus.OK.value())
                            .message("User registered to bootcamps successfully")
                            .path(request.path())
                            .timestamp(LocalDateTime.now())
                            .build();
                    return ServerResponse.ok().bodyValue(response);
                }));
    }
}
