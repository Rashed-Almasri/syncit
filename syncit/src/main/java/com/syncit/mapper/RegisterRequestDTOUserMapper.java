package com.syncit.mapper;

import com.syncit.DTO.RegisterRequestDTO;
import com.syncit.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RegisterRequestDTOUserMapper {
    User RegisterRequestDTOToUser(RegisterRequestDTO registerRequestDTO);
}
