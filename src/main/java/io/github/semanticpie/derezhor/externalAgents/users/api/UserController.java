package io.github.semanticpie.derezhor.externalAgents.users.api;


import io.github.semanticpie.derezhor.externalAgents.users.dtos.GenreDTO;
import io.github.semanticpie.derezhor.externalAgents.users.dtos.UserDTO;
import io.github.semanticpie.derezhor.externalAgents.users.services.GenreService;
import io.github.semanticpie.derezhor.externalAgents.users.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/api/v1/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    private GenreService genreService;

    @GetMapping()
    List<UserDTO> index() {
        return List.of(
                UserDTO.builder()
                        .uuid("9c858901-8a57-4791-81fe-4c455b099bc9")
                        .username("badmood")
                        .build(),
                UserDTO.builder()
                        .uuid("f47ac10b-58cc-4372-a567-0e02b2c3d479")
                        .username("glebchanskiy")
                        .build(),
                UserDTO.builder()
                        .uuid("6fa459ea-ee8a-3ca4-894e-db77e160355e")
                        .username("ardonplay")
                        .build()
        );
    }

    @GetMapping("/{uuid}")
    UserDTO findUser(@PathVariable String uuid) {
        return UserDTO.builder()
                .uuid("9c858901-8a57-4791-81fe-4c455b099bc9")
                .username("badmood")
                .build();
    }

    /*@PostMapping()
    ResponseEntity<UserDTO> addUser(@RequestBody InitUserDTO initUserDTO) {
        try {
            UserDTO userDTO = userService.createNewUser(initUserDTO.getUsername()).orElseThrow();
            genreService.addGenres(userDTO.getUuid(), initUserDTO.getFavoriteGenres());
            return ResponseEntity.ok(userDTO);
        } catch (Exception ex) {
            log.error(Arrays.toString(ex.getStackTrace()));
            return ResponseEntity.status(500).build();
        }
    }*/



    @PostMapping("/test")
    ResponseEntity<?> test(@RequestBody GenreDTO genreDTO) {
        try {

            String userUUID = userService.getUserUUID(genreDTO.getName()).orElseThrow();
            System.out.println();
            return ResponseEntity.ok(userUUID);
        } catch (Exception ex) {
            log.error(Arrays.toString(ex.getStackTrace()));
            return ResponseEntity.status(500).build();
        }
    }


    @DeleteMapping("/{uuid}")
    ResponseEntity<UserDTO> deleteUser(@PathVariable String uuid) {
        return ResponseEntity.ok(
                UserDTO.builder()
                        .uuid(uuid)
                        .username("Deleted User Name")
                        .build()
        ); // return deleted entity
    }

}

