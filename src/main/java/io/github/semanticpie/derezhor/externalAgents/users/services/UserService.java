package io.github.semanticpie.derezhor.externalAgents.users.services;

import io.github.semanticpie.derezhor.externalAgents.users.models.UserDTO;

import java.util.Optional;

public interface UserService {

    /**
     * UserDTO contains field uuid which is name of a struct Node
     *
     * @param username user's nickname
     * @return An Optional containing UserDTO
     */
    Optional<UserDTO> createUser(String username);
}
