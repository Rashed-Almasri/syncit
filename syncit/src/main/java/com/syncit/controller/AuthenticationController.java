package com.syncit.controller;

import com.syncit.DTO.AuthenticationRequestDTO;
import com.syncit.DTO.AuthenticationResponseDTO;
import com.syncit.DTO.RegisterRequestDTO;
import com.syncit.repository.UserRepository;
import com.syncit.security.SecurityUtil;
import com.syncit.service.AuthenticationService;
import com.syncit.service.AuthorizationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @Autowired
    AuthorizationService authorizationService;
    @Autowired
    UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        AuthenticationResponseDTO token=authenticationService.register(registerRequestDTO);
        return ResponseEntity.ok().body(token);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationRequestDTO request) {
        AuthenticationResponseDTO token=authenticationService.authenticate(request);
        return ResponseEntity.ok().body(token);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> register(HttpServletResponse response) {

        authenticationService.logout();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/canedit")
    public ResponseEntity<?> canEdit(@RequestBody Map<String, Long> request) throws Exception {
        Long projectId = request.get("projectId");
        authorizationService.isAuthorizedToEdit(
                userRepository.findByUsername(SecurityUtil.getCurrentUsername()).get().getId(),
                projectId
        );
        return ResponseEntity.ok().build();
    }

}
