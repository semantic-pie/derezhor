package io.github.semanticpie.derezhor.externalAgents.users.models;

import lombok.Data;

import java.util.List;

@Data
public class InitUserDTO {
    private String username;
    private List<GenreDTO> favoriteGenres;
}
