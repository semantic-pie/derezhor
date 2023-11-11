package io.github.semanticpie.derezhor.externalAgents.users.services.impl;

import io.github.semanticpie.derezhor.externalAgents.users.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ostis.api.context.DefaultScContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@Slf4j
@AllArgsConstructor
public class AuthServiceImpl {

    private final DefaultScContext context;

    private final UserService userService;

   /* public ResponseEntity<?> createNewUser(@RequestBody RegistrationDTO registrationDTO) {
        if ()
    }*/


}
