package io.github.semanticpie.derezhor.externalAgents.users.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private String uuid;
    private String username;
}
