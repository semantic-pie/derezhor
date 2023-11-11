package io.github.semanticpie.derezhor.externalAgents.users.api;

import io.github.semanticpie.derezhor.externalAgents.users.models.SignUpUserDTO;
import io.github.semanticpie.derezhor.externalAgents.users.services.GenreService;
import io.github.semanticpie.derezhor.externalAgents.users.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@Slf4j
@RequestMapping(path = "/pie-tunes/api/v1")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;
    private final GenreService genreService;

    @PostMapping("/signup")
    ResponseEntity<?> createNewUser(@RequestBody SignUpUserDTO signUpUserDTO) {
        try {
            var user = userService.createUser(
                    signUpUserDTO.getUsername(),
                    signUpUserDTO.getPassword(),
                    signUpUserDTO.getUserRole()).orElseThrow();
            genreService.addGenres(user.getUuid(), signUpUserDTO.getFavoriteGenres());
            return ResponseEntity.ok(user);
        } catch (Exception ex) {
            log.error(Arrays.toString(ex.getStackTrace()));
            return ResponseEntity.status(500).build();
        }




    }
}
