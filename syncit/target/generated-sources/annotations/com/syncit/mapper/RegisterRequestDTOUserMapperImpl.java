package com.syncit.mapper;

import com.syncit.DTO.RegisterRequestDTO;
import com.syncit.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-04T17:28:27+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 22.0.1 (Oracle Corporation)"
)
@Component
public class RegisterRequestDTOUserMapperImpl implements RegisterRequestDTOUserMapper {

    @Override
    public User RegisterRequestDTOToUser(RegisterRequestDTO registerRequestDTO) {
        if ( registerRequestDTO == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.username( registerRequestDTO.getUsername() );
        user.password( registerRequestDTO.getPassword() );
        user.email( registerRequestDTO.getEmail() );

        return user.build();
    }
}
