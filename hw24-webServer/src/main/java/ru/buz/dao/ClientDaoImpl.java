package ru.buz.dao;

import ru.buz.core.repository.DataTemplate;
import ru.buz.core.sessionmanager.TransactionManager;
import ru.buz.crm.model.Client;

import java.util.List;
import java.util.Optional;

public class ClientDaoImpl implements ClientDao {

    private final DataTemplate<Client> clientDataTemplate;
    private final TransactionManager transactionManager;

    public ClientDaoImpl(DataTemplate<Client> clientDataTemplate, TransactionManager transactionManager) {
        this.clientDataTemplate = clientDataTemplate;
        this.transactionManager = transactionManager;
    }


    @Override
    public Optional<Client> findById(long id) {
        return transactionManager.doInReadOnlyTransaction(session -> clientDataTemplate.findById(session, id));
    }

    @Override
    public Optional<Client> findRandomUser() {
        return Optional.empty();
    }

    @Override
    public Optional<Client> findByLogin(String login) {
        return Optional.empty();
    }

    @Override
    public List<Client> findAll() {
        return null;
    }

    @Override
    public Optional<Client> createOrUpdate(Client client) {
        var clientCloned = client.clone();

       return transactionManager.doInTransaction(session -> {
            if(client.getId()==null) {
                clientDataTemplate.insert(session, clientCloned);
                return Optional.of(clientCloned);
            }
            clientDataTemplate.update(session, clientCloned);
            return Optional.of(clientCloned);
        });
    }

    @Override
    public boolean delete(long id) {
        return false;
    }
}
