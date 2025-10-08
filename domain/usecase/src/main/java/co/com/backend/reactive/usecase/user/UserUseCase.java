package co.com.backend.reactive.usecase.user;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import co.com.backend.reactive.model.bootcampdata.BootcampData;
import co.com.backend.reactive.model.bootcampdata.gateways.BootcampDataRepository;
import co.com.backend.reactive.model.datatosend.DataToSend;
import co.com.backend.reactive.model.datatosend.gateways.DataToSendRepository;
import co.com.backend.reactive.model.user.User;
import co.com.backend.reactive.model.user.gateways.UserRepository;
import co.com.backend.reactive.model.userboocamp.UserBootcamp;
import co.com.backend.reactive.model.userboocamp.gateways.UserBootcampRepository;
import co.com.backend.reactive.usecase.user.exceptions.BusinessException;
import co.com.backend.reactive.usecase.user.enums.UserError;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase implements IUserUseCase {

    private final UserRepository userRepository;
    private final UserBootcampRepository userBootcampRepository;
    private final BootcampDataRepository bootcampDataRepository;
    private final DataToSendRepository dataToSendRepository;

    @Override
    public Mono<User> save(User user) {
        return userRepository.findByEmail(user.getEmail())
                .flatMap(exist -> Mono.<User>error(
                        new BusinessException(UserError.USER_EMAIL_ALREADY_EXISTS.getMessage())))
                .switchIfEmpty(
                        Mono.defer(() -> userRepository.save(user)
                                .switchIfEmpty(
                                        Mono.error(new BusinessException(UserError.USER_NOT_CREATED.getMessage())))));
    }

    @Override
    public Mono<Void> registerUserBootcamp(Long userId, List<Long> bootcampIds) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new BusinessException(UserError.USER_NOT_FOUND.getMessage())))
                .flatMap(user ->
                Flux.fromIterable(bootcampIds)
                        .flatMap(bootcampId -> bootcampDataRepository.findById(bootcampId)
                                .switchIfEmpty(Mono.error(new BusinessException(
                                        UserError.BOOTCAMP_NOT_FOUND.getMessage() + ": " + bootcampId))))
                        .collectList()
                        .flatMap(newBootcamps ->
                        userBootcampRepository.findByUserId(userId)
                                .map(UserBootcamp::getBootcampId)
                                .collectList()
                                .flatMap(registeredBootcampIds -> {
                                    List<Long> duplicates = bootcampIds.stream()
                                            .filter(registeredBootcampIds::contains)
                                            .toList();
                                    if (!duplicates.isEmpty()) {
                                        return Mono.<Void>error(new BusinessException(
                                                UserError.USER_ALREADY_HAS_BOOTCAMPS.getMessage() + ": " + duplicates));
                                    }
                                    return Flux.fromIterable(registeredBootcampIds)
                                            .flatMap(bootcampDataRepository::findById)
                                            .collectList()
                                            .flatMap(currentBootcamps -> validateConflicts(currentBootcamps,
                                                    newBootcamps)
                                                    .thenMany(Flux.fromIterable(bootcampIds)
                                                            .map(bootcampId -> UserBootcamp.builder()
                                                                    .userId(userId)
                                                                    .bootcampId(bootcampId)
                                                                    .build())
                                                            .flatMap(userBootcampRepository::registerUserBootcamp))
                                                    .then(Mono.defer(() -> {
                                                        DataToSend data = DataToSend.builder()
                                                                .userId(userId)
                                                                .email(user.getEmail())
                                                                .name(user.getName())
                                                                .bootcampIds(bootcampIds)
                                                                .build();
                                                        return dataToSendRepository.sendDataToSqs(data);
                                                    })));
                                })));
    }

    private Mono<Void> validateConflicts(List<BootcampData> currentBootcamps, List<BootcampData> newBootcamps) {
        return Flux.fromIterable(newBootcamps)
                .flatMap(newBootcamp -> {
                    List<BootcampData> others = Stream.concat(
                            currentBootcamps.stream(),
                            newBootcamps.stream().filter(other -> !other.equals(newBootcamp))
                    ).toList();
                    return Flux.fromIterable(others)
                            .filter(other -> isConflictDates(newBootcamp, other))
                            .next(); 
                })
                .next()
                .flatMap(conflict ->
                        Mono.<Void>error(new BusinessException(UserError.CONFLICT_DATES.getMessage()))
                )
                .switchIfEmpty(Mono.empty());
    }

    
    private boolean isConflictDates(BootcampData a, BootcampData b) {
        LocalDate aStart = a.getStartDate();
        LocalDate aEnd = aStart.plusDays(a.getDurationInDays());
        LocalDate bStart = b.getStartDate();
        LocalDate bEnd = bStart.plusDays(b.getDurationInDays());
        return !aStart.isAfter(bEnd) && !bStart.isAfter(aEnd);
    }

}
