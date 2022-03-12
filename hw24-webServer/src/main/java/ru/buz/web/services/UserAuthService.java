package ru.buz.web.services;

import ru.buz.crm.model.Role;

import java.util.Optional;

public interface UserAuthService {
    Optional<Role> authenticate(String login, String password);
}
