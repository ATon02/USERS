package co.com.backend.reactive.usecase.user;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import co.com.backend.reactive.model.bootcampdata.BootcampData;
import co.com.backend.reactive.model.bootcampdata.gateways.BootcampDataRepository;
import co.com.backend.reactive.model.user.User;
import co.com.backend.reactive.model.user.gateways.UserRepository;
import co.com.backend.reactive.model.userboocamp.UserBootcamp;
import co.com.backend.reactive.model.userboocamp.gateways.UserBootcampRepository;
import co.com.backend.reactive.usecase.user.enums.UserError;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserBootcampRepository userBootcampRepository;
    
    @Mock
    private BootcampDataRepository bootcampDataRepository;
    
    private UserUseCase userUseCase;
    
    @BeforeEach
    void setUp() {
        userUseCase = new UserUseCase(userRepository, userBootcampRepository, bootcampDataRepository);
    }
    
    @Test
    void save_WhenEmailNotExists_ShouldSaveUser() {
        User user = User.builder().name("John Doe").email("john@example.com").build();
        User savedUser = User.builder().id(1L).name("John Doe").email("john@example.com").build();
        
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));
        
        StepVerifier.create(userUseCase.save(user))
                .expectNext(savedUser)
                .verifyComplete();
    }
    
    @Test
    void save_WhenEmailExists_ShouldReturnError() {
        User user = User.builder().name("John Doe").email("john@example.com").build();
        User existingUser = User.builder().id(1L).name("Jane").email("john@example.com").build();
        
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(existingUser));
        
        StepVerifier.create(userUseCase.save(user))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().equals(UserError.USER_EMAIL_ALREADY_EXISTS.getMessage()))
                .verify();
    }
    
    @Test
    void save_WhenSaveFails_ShouldReturnError() {
        User user = User.builder().name("John Doe").email("john@example.com").build();
        
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(userRepository.save(any(User.class))).thenReturn(Mono.empty());
        
        StepVerifier.create(userUseCase.save(user))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().equals(UserError.USER_NOT_CREATED.getMessage()))
                .verify();
    }
    
    @Test
    void registerUserBootcamp_WhenUserIdIsNull_ShouldReturnError() {
        List<Long> bootcampIds = Arrays.asList(1L, 2L);
        
        StepVerifier.create(userUseCase.registerUserBootcamp(null, bootcampIds))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().equals(UserError.USER_ID_REQUIRED.getMessage()))
                .verify();
    }
    
    @Test
    void registerUserBootcamp_WhenBootcampListIsNull_ShouldReturnError() {
        Long userId = 1L;
        
        StepVerifier.create(userUseCase.registerUserBootcamp(userId, null))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().equals(UserError.BOOTCAMP_LIST_EMPTY.getMessage()))
                .verify();
    }
    
    @Test
    void registerUserBootcamp_WhenBootcampListIsEmpty_ShouldReturnError() {
        Long userId = 1L;
        List<Long> bootcampIds = Collections.emptyList();
        
        StepVerifier.create(userUseCase.registerUserBootcamp(userId, bootcampIds))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().equals(UserError.BOOTCAMP_LIST_EMPTY.getMessage()))
                .verify();
    }
    
    @Test
    void registerUserBootcamp_WhenBootcampListTooLarge_ShouldReturnError() {
        Long userId = 1L;
        List<Long> bootcampIds = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L);
        
        StepVerifier.create(userUseCase.registerUserBootcamp(userId, bootcampIds))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().equals(UserError.BOOTCAMP_LIST_TOO_LARGE.getMessage()))
                .verify();
    }
    
    @Test
    void registerUserBootcamp_WhenUserNotFound_ShouldReturnError() {
        Long userId = 1L;
        List<Long> bootcampIds = Arrays.asList(1L, 2L);
        
        when(userRepository.findById(anyLong())).thenReturn(Mono.empty());
        
        StepVerifier.create(userUseCase.registerUserBootcamp(userId, bootcampIds))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().equals(UserError.USER_NOT_FOUND.getMessage()))
                .verify();
    }
    
    @Test
    void registerUserBootcamp_WhenUserAlreadyHasBootcamp_ShouldReturnError() {
        Long userId = 1L;
        List<Long> bootcampIds = Arrays.asList(1L, 2L);
        User user = User.builder().id(userId).name("John").email("john@example.com").build();
        UserBootcamp existingBootcamp = UserBootcamp.builder().userId(userId).bootcampId(1L).build();
        
        when(userRepository.findById(anyLong())).thenReturn(Mono.just(user));
        when(bootcampDataRepository.findById(1L)).thenReturn(Mono.just(BootcampData.builder().id(1L).build()));
        when(bootcampDataRepository.findById(2L)).thenReturn(Mono.just(BootcampData.builder().id(2L).build()));
        when(userBootcampRepository.findByUserId(anyLong())).thenReturn(Flux.just(existingBootcamp));
        
        StepVerifier.create(userUseCase.registerUserBootcamp(userId, bootcampIds))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().equals(UserError.USER_ALREADY_HAS_BOOTCAMPS.getMessage()))
                .verify();
    }
    
    @Test
    void registerUserBootcamp_WhenAllValidationsPass_ShouldRegisterSuccessfully() {
        Long userId = 1L;
        List<Long> bootcampIds = Arrays.asList(1L, 2L);
        User user = User.builder().id(userId).name("John").email("john@example.com").build();
        
        when(userRepository.findById(anyLong())).thenReturn(Mono.just(user));
        when(bootcampDataRepository.findById(1L)).thenReturn(Mono.just(BootcampData.builder().id(1L).build()));
        when(bootcampDataRepository.findById(2L)).thenReturn(Mono.just(BootcampData.builder().id(2L).build()));
        when(userBootcampRepository.findByUserId(anyLong())).thenReturn(Flux.empty());
        when(userBootcampRepository.registerUserBootcamp(any(UserBootcamp.class))).thenReturn(Mono.empty());
        
        StepVerifier.create(userUseCase.registerUserBootcamp(userId, bootcampIds))
                .verifyComplete();
    }
    
    @Test
    void registerUserBootcamp_WhenMaxBootcamps_ShouldRegisterSuccessfully() {
        Long userId = 1L;
        List<Long> bootcampIds = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        User user = User.builder().id(userId).name("John").email("john@example.com").build();
        
        when(userRepository.findById(anyLong())).thenReturn(Mono.just(user));
        when(bootcampDataRepository.findById(any())).thenReturn(Mono.just(BootcampData.builder().build()));
        when(userBootcampRepository.findByUserId(anyLong())).thenReturn(Flux.empty());
        when(userBootcampRepository.registerUserBootcamp(any(UserBootcamp.class))).thenReturn(Mono.empty());
        
        StepVerifier.create(userUseCase.registerUserBootcamp(userId, bootcampIds))
                .verifyComplete();
    }
}
