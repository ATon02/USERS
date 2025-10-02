package co.com.backend.reactive.api.mapper;

import co.com.backend.reactive.model.user.User;
import co.com.backend.reactive.api.dtos.request.UserRequestDTO;
import co.com.backend.reactive.api.dtos.response.UserResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDTOMapper {

    User toModel(UserRequestDTO requestDTO);

    UserResponseDTO toResponseDTO(User user);
}