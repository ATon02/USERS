package co.com.backend.reactive.api;

import co.com.backend.reactive.api.dtos.request.RegisterBootcampRequest;
import co.com.backend.reactive.api.dtos.request.UserRequestDTO;
import co.com.backend.reactive.api.dtos.response.BaseResponse;
import co.com.backend.reactive.api.helper.ConstHandler;
import co.com.backend.reactive.api.helper.ValidatorRequest;
import co.com.backend.reactive.api.mapper.UserDTOMapper;
import co.com.backend.reactive.usecase.user.IUserUseCase;
import co.com.backend.reactive.usecase.user.enums.UserError;
import co.com.backend.reactive.usecase.user.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final ValidatorRequest validatorRequest;

    public Mono<ServerResponse> saveUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRequestDTO.class)
                .doOnNext(validatorRequest::validateDTOUser)
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
        String userId = request.pathVariable(ConstHandler.Id_PARAM.getParameterName());
        return Mono.fromCallable(() -> Long.parseLong(userId))
                .onErrorMap(NumberFormatException.class,
                        ex -> new BusinessException(UserError.USER_ID_INVALID.getMessage()))
                .zipWith(request.bodyToMono(RegisterBootcampRequest.class))
                .doOnNext(tuple -> validatorRequest.validateDTORegisterBootcamp(tuple.getT2()))
                .flatMap(tuple -> {
                    Long id = tuple.getT1();
                    RegisterBootcampRequest bootcampRequest = tuple.getT2();
                    return userUseCase.registerUserBootcamp(id, bootcampRequest.getBootcampIds());
                })
                .then(Mono.defer(() -> {
                    BaseResponse<Void> response = BaseResponse.<Void>builder()
                            .status(HttpStatus.OK.value())
                            .message("User registered to bootcamps successfully")
                            .path(request.path())
                            .timestamp(LocalDateTime.now())
                            .build();
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response);
                }))
                ;
    }
}
