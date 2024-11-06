package com.syncit.service;


import com.syncit.DTO.AuthenticationRequestDTO;
import com.syncit.DTO.AuthenticationResponseDTO;
import com.syncit.DTO.RegisterRequestDTO;

public interface AuthenticationService {
    public AuthenticationResponseDTO register(RegisterRequestDTO requestAuthenticationResponseDTO);
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request);
    public void logout();
}
