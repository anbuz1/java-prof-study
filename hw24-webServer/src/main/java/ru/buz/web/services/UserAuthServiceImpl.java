package ru.buz.web.services;

import ru.buz.crm.model.Role;
import ru.buz.dao.UserDao;

import java.util.Optional;

public class UserAuthServiceImpl implements UserAuthService {

    private final UserDao userDao;

    public UserAuthServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Optional<Role> authenticate(String login, String password) {
        return userDao.findByLogin(login)
                .map(user -> {
                    if (user.getPassword().equals(password)) {
                        return user.getRole();
                    } else return null;
                });
    }

}
