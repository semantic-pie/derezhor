package io.github.semanticpie.derezhor.externalAgents.users.models;

import io.github.semanticpie.derezhor.externalAgents.users.models.enums.UserRole;
import lombok.Data;

import java.util.List;

@Data
public class SignUpUserDTO {
    private String username;
    private String password;
    private UserRole userRole;
    private List<GenreDTO> favoriteGenres;
}
