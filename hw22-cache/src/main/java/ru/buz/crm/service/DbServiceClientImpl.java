package ru.buz.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.buz.core.localcache.exceptions.PutInCacheException;
import ru.buz.core.localcache.interfaces.BuzCache;
import ru.buz.core.repository.DataTemplate;
import ru.buz.crm.model.Client;
import ru.buz.core.sessionmanager.TransactionManager;

import java.util.List;
import java.util.Optional;

public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);

    private final DataTemplate<Client> clientDataTemplate;
    private final TransactionManager transactionManager;
    private final BuzCache buzCache;

    public DbServiceClientImpl(TransactionManager transactionManager, DataTemplate<Client> clientDataTemplate, BuzCache buzCache) {
        this.transactionManager = transactionManager;
        this.clientDataTemplate = clientDataTemplate;
        this.buzCache = buzCache;
    }

    @Override
    public Client saveClient(Client client) {
        Client clientResult = transactionManager.doInTransaction(session -> {
            var clientCloned = client.clone();
            if (client.getId() == null) {
                clientDataTemplate.insert(session, clientCloned);
                log.info("created client: {}", clientCloned);
                return clientCloned;
            }
            clientDataTemplate.update(session, clientCloned);
            log.info("updated client: {}", clientCloned);
            return clientCloned;
        });
        try {
            buzCache.add(clientResult);
        } catch (PutInCacheException e) {
            log.info(e.toString());
        }
        return clientResult;
    }

    @Override
    public Optional<Client> getClient(long id) {
        Optional<Client> client = buzCache.get(id, Client.class);
        if(client.isEmpty()){
        return transactionManager.doInReadOnlyTransaction(session -> {
            var clientOptional = clientDataTemplate.findById(session, id);
            log.info("client: {}", clientOptional);
            try {
                buzCache.add(clientOptional.get());
            } catch (PutInCacheException e) {
                log.info(e.toString());
            }
            return clientOptional;
        });}
        return client;
    }
    @Override
    public List<Client> getClient(String fieldName, Object value) {
        return transactionManager.doInReadOnlyTransaction(session -> {
            var clientList = clientDataTemplate.findByEntityField(session,fieldName,value);
            log.info("clientList:{}", clientList);
            return clientList;
        });
    }

    @Override
    public List<Client> findAll() {
        return transactionManager.doInReadOnlyTransaction(session -> {
            var clientList = clientDataTemplate.findAll(session);
            log.info("clientList:{}", clientList);
            return clientList;
       });
    }
}
