package io.github.semanticpie.derezhor.externalAgents.users.services.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
