package io.github.semanticpie.derezhor.externalAgents.users.api;

import io.github.semanticpie.derezhor.externalAgents.users.dtos.AuthRequest;
import io.github.semanticpie.derezhor.externalAgents.users.dtos.SignUpRequest;
import io.github.semanticpie.derezhor.externalAgents.users.services.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@RequestMapping(path = "/api/v1/derezhor")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> createNewUser(@RequestBody SignUpRequest signUpRequest) {
        return authService.createNewUser(signUpRequest);
    }

    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody AuthRequest authRequest) {
        return authService.createAuthToken(authRequest);
    }


}
