package ru.buz.dao;

import ru.buz.core.repository.DataTemplate;
import ru.buz.core.sessionmanager.TransactionManager;
import ru.buz.crm.model.Client;
import ru.buz.crm.model.User;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    private final DataTemplate<User> userDataTemplate;
    private final TransactionManager transactionManager;

    public UserDaoImpl(DataTemplate<User> userDataTemplate, TransactionManager transactionManager) {
        this.userDataTemplate = userDataTemplate;
        this.transactionManager = transactionManager;
    }


    @Override
    public Optional<User> findById(long id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findRandomUser() {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByLogin(String login) {
       var result = transactionManager.doInReadOnlyTransaction(session ->
               userDataTemplate.findByEntityField(session,"login",login));
        if(result.size()!=1){
            return Optional.empty();
        }else {
            return Optional.ofNullable(result.get(0));
        }
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public Optional<User> update(User User) {
        return Optional.empty();
    }

    @Override
    public boolean create(User User) {
        return false;
    }

    @Override
    public boolean delete(long id) {
        return false;
    }
}
