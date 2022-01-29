package ru.buz.demo;

import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.buz.core.localcache.exceptions.PutInCacheException;
import ru.buz.core.localcache.implementations.BuzCacheImpl;
import ru.buz.core.localcache.implementations.SimpleBuzCacheImpl;
import ru.buz.core.localcache.interfaces.BuzCache;
import ru.buz.core.repository.DataTemplateHibernate;
import ru.buz.core.repository.HibernateUtils;
import ru.buz.core.sessionmanager.TransactionManagerHibernate;
import ru.buz.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.buz.crm.model.Address;
import ru.buz.crm.model.Client;
import ru.buz.crm.model.Phone;
import ru.buz.crm.service.DbServiceClientImpl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.buz.core.localcache.implementations.CacheManager.getBuzCacheInstance;


public class DbServiceDemo {

    private static final Logger log = LoggerFactory.getLogger(DbServiceDemo.class);

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";
    public static final int CLIENT_NUMBER = 50000;

    public static void main(String[] args) throws NoSuchFieldException {

        var buzCache = new BuzCacheImpl(Client.class, Address.class, Phone.class);
        var buzCachesimple = new SimpleBuzCacheImpl(Client.class, Address.class, Phone.class);

//        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

//        var dbUrl = configuration.getProperty("hibernate.connection.url");
//        var dbUserName = configuration.getProperty("hibernate.connection.username");
//        var dbPassword = configuration.getProperty("hibernate.connection.password");
//
//        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();
//
//        var sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);
//
//        var transactionManager = new TransactionManagerHibernate(sessionFactory);
/////
//        var clientTemplate = new DataTemplateHibernate<>(Client.class);
///
//        var dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate);
        for (int i = 1; i <= CLIENT_NUMBER; i++) {
            Client tempClient = new Client((long) i, "Vasya_" + i, new Address(null, "AnyStreet"), List.of(new Phone(null, "13-555-22"), new Phone(null, "13-555-23")));
//            dbServiceClient.saveClient(tempClient);
            try {
                buzCache.add(tempClient.getId(), tempClient);
                buzCachesimple.add(tempClient.getId(), tempClient);
            } catch (PutInCacheException e) {
                e.printStackTrace();
            }
        }


        System.out.println("--------------------------------Start select from SIMPLE cache-----------------------------------");
        long start = System.currentTimeMillis();
        int count = 0;
        for (int i = 1; i <= CLIENT_NUMBER; i++) {
            Client tempClient = buzCachesimple.get("name", "Vasya_" + i, Client.class).get(0);
        }
        System.out.println();
        long end = System.currentTimeMillis();
        System.out.println("TOTAL TIME: " + (end - start));
        System.out.println("--------------------------------End select from cache-------------------------------------");

        System.out.println("--------------------------------Start2 select from MAIN cache-----------------------------------");
        start = System.currentTimeMillis();
        count = 0;
        for (int i = 1; i <= CLIENT_NUMBER; i++) {
            Client tempClient = buzCache.get("name", "Vasya_" + i, Client.class).get(0);
        }
        System.out.println();
        end = System.currentTimeMillis();
        System.out.println("TOTAL TIME: " + (end - start));
        System.out.println("--------------------------------End2 select from cache-------------------------------------");



//        dbServiceClient.saveClient(new Client(null, "Vasya", new Address(null, "AnyStreet"), List.of(new Phone(null, "13-555-22"), new Phone(null, "13-555-23"))));
//
//        var clientSecond = dbServiceClient.saveClient(new Client(null, "Petya", new Address(null, "AnyStreet2"), List.of(new Phone(null, "13-555-22"), new Phone(null, "13-555-23"))));
//        var clientSecondSelected = dbServiceClient.getClient(clientSecond.getId())
//                .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecond.getId()));
//        log.info("clientSecondSelected:{}", clientSecondSelected);
/////
//        dbServiceClient.saveClient(new Client(clientSecondSelected.getId(), "dbServiceSecondUpdated", new Address(null, "AnyStreet2"), List.of(new Phone(null, "13-555-22"))));
//        var clientUpdated = dbServiceClient.getClient(clientSecondSelected.getId())
//                .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecondSelected.getId()));
//        log.info("clientUpdated:{}", clientUpdated);
//
//        log.info("All clients");
//        dbServiceClient.findAll().forEach(client -> log.info("client:{}", client));
    }
}
