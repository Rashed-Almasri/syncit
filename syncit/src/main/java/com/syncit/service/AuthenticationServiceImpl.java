package com.syncit.service;

import com.syncit.DTO.AuthenticationRequestDTO;
import com.syncit.DTO.AuthenticationResponseDTO;
import com.syncit.DTO.RegisterRequestDTO;
import com.syncit.enums.Role;
import com.syncit.exception.UserAlreadyExistsException;
import com.syncit.mapper.RegisterRequestDTOUserMapper;
import com.syncit.model.User;
import com.syncit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RegisterRequestDTOUserMapper registerRequestDTOUserMapper;

    public AuthenticationResponseDTO register(RegisterRequestDTO request) {
        User user = registerRequestDTOUserMapper.RegisterRequestDTOToUser(request);
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        validateUserNotExists(request);
        userRepository.save(user);
        String jwtToken = generateJwt(user.getUsername());
        int jwtExpire = 60*60;
        return setJWTCookie(jwtToken,jwtExpire);
    }

    private void validateUserNotExists(RegisterRequestDTO request) {
        userRepository.findUserByUsernameOrEmail(request.getUsername(),request.getEmail())
                .ifPresent((item) -> {
                    throw new UserAlreadyExistsException("User with username " + item.getUsername() + " already exists");
                });
    }

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        String username = request.getUsername();
        String jwtToken = generateJwt(username);
        int jwtExpire = 100000000;
        return setJWTCookie(jwtToken,jwtExpire);
    }

    @Override
    public void logout() {

    }

    private AuthenticationResponseDTO setJWTCookie(String token, int duration){
        String bearerToken = "Bearer "+token;
        return AuthenticationResponseDTO.builder().token(bearerToken).build();
    }

    private String generateJwt(String username) {
        String jwtToken = jwtService.generateToken(username);
        return jwtToken;
    }

}