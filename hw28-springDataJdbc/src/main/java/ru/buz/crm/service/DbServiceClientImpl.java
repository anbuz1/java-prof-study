package ru.buz.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.buz.crm.model.Client;
import ru.buz.crm.repository.ClientRepository;
import ru.buz.crm.session.TransactionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);
    private final ClientRepository clientRepository;
    private final TransactionManager transactionManager;

    public DbServiceClientImpl(ClientRepository clientRepository, TransactionManager transactionManager) {
        this.clientRepository = clientRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    public Client saveClient(Client client) {
        return transactionManager.doInTransaction(() -> {
            var clientCloned = client.clone();
            clientCloned = clientRepository.save(clientCloned);
            log.info("created client: {}", clientCloned);
            return clientCloned;
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        return transactionManager.doInTransaction(() -> {
            var clientOptional = clientRepository.findById(id);
            log.info("client: {}", clientOptional);
            return clientOptional;
        });
    }

    @Override
    public List<Client> findAll() {
        return transactionManager.doInTransaction(() -> {
            var clients = clientRepository.findAll();
            List<Client> clientList = new ArrayList<>();
            clients.iterator().forEachRemaining(clientList::add);
            log.info("clientList:{}", clientList);
            return clientList;
        });
    }
}
