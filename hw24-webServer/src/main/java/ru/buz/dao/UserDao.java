package ru.buz.dao;

import ru.buz.crm.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> findById(long id);
    Optional<User> findRandomUser();
    Optional<User> findByLogin(String login);
    List<User> findAll();
    Optional<User> update(User User);
    boolean create(User User);
    boolean delete(long id);
}
