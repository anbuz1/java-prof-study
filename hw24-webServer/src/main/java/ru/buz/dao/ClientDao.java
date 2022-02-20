package ru.buz.dao;

import ru.buz.crm.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientDao {
    Optional<Client> findById(long id);
    Optional<Client> findRandomUser();
    Optional<Client> findByLogin(String login);
    List<Client> findAll();
    Optional<Client> createOrUpdate(Client client);
    boolean delete(long id);
}
