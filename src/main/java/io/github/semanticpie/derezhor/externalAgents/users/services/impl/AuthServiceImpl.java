package io.github.semanticpie.derezhor.externalAgents.users.services.impl;

import io.github.semanticpie.derezhor.externalAgents.users.dtos.AuthRequest;
import io.github.semanticpie.derezhor.externalAgents.users.dtos.SignUpRequest;
import io.github.semanticpie.derezhor.externalAgents.users.services.AuthService;
import io.github.semanticpie.derezhor.externalAgents.users.services.GenreService;
import io.github.semanticpie.derezhor.externalAgents.users.services.UserService;
import io.github.semanticpie.derezhor.externalAgents.users.services.exceptions.UserAlreadyExistsException;
import io.github.semanticpie.derezhor.externalAgents.users.services.utils.JwtTokenProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Arrays;
import java.util.NoSuchElementException;

@Service
@Slf4j
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final GenreService genreService;
    private final AuthenticationManager authenticationManager;


    @Override
    public ResponseEntity<?> createNewUser(@RequestBody SignUpRequest signUpRequest) {
        var username = signUpRequest.getUsername();

        if (userService
                .getUsernameScLink(username)
                .isPresent()) {
            var msg = String.format("Username '%s' is already taken", username);
            log.info(msg);
            throw new UserAlreadyExistsException(msg);
        }

        try {
            var user = userService
                    .createUser(username, signUpRequest.getPassword(), signUpRequest.getUserRole())
                    .orElseThrow();
            genreService.addGenres(user.getUuid(), signUpRequest.getFavoriteGenres());
            return ResponseEntity.ok(user);

        } catch (NoSuchElementException ex) {
            log.error(Arrays.toString(ex.getStackTrace()));
            return ResponseEntity.status(500).build();
        }
    }

    public ResponseEntity<?> createAuthToken(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(), authRequest.getPassword()
                    ));
        } catch (BadCredentialsException ex) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);// замеееннииить !!!!!!!!
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenProvider.generateToken(userDetails);
    }




}
