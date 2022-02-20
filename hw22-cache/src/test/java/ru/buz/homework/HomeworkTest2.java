package ru.buz.homework;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.buz.core.localcache.exceptions.PutInCacheException;
import ru.buz.core.localcache.implementations.CacheManager;
import ru.buz.core.localcache.interfaces.BuzCache;
import ru.buz.crm.model.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static ru.buz.core.localcache.implementations.CacheManager.getBuzCacheInstance;

class HomeworkTest2 {

    private StandardServiceRegistry serviceRegistry;
    private Metadata metadata;
    private SessionFactory sessionFactory;
    private BuzCache buzCache;

    // Это надо раскомментировать, у выполненного ДЗ, все тесты должны проходить
    // Кроме удаления комментирования, тестовый класс менять нельзя

    @BeforeEach
    public void setUp() {
        makeTestDependencies();
        buzCache = getBuzCacheInstance(Phone.class, Client.class, Address.class);
    }

    @AfterEach
    public void tearDown() {
        sessionFactory.close();
    }


    @Test
    public void testHomeworkRequirementsForTablesCount() {

        var tables = StreamSupport.stream(metadata.getDatabase().getNamespaces().spliterator(), false)
                .flatMap(namespace -> namespace.getTables().stream())
                .collect(Collectors.toList());
        assertThat(tables).hasSize(3);
    }

    @Test
    public void testHomeworkRequirementsForUpdatesCount() throws PutInCacheException {
        applyCustomSqlStatementLogger(new SqlStatementLogger(true, false, false, 0) {
            @Override
            public void logStatement(String statement) {
                assertThat(statement).doesNotContain("update");
                super.logStatement(statement);
            }
        });

        var client = new Client(null, "Vasya", new Address(null, "AnyStreet"), List.of(new Phone(null, "13-555-22"), new Phone(null, "13-555-23")));
        try (var session = sessionFactory.openSession()) {
            session.getTransaction().begin();
            session.persist(client);
            session.getTransaction().commit();

            session.clear();

            var loadedClient = session.find(Client.class, 1L);

            assertThat(loadedClient).usingRecursiveComparison().isEqualTo(client);
        }
    }

    @Test
    public void testCacheLibWork() throws PutInCacheException {

        Client testClient = null;
        Address testAddress = null;
        List<Phone> phones = List.of(new Phone(1L, "13-555-22"), new Phone(2L, "13-555-23"));
        for (int i = 1; i <= 500; i++) {
            Address tempAddress =  new Address((long) i, "AnyStreet");

            Client tempClient = new Client((long) i, "Vasili_" + i,
                    tempAddress,
                    phones);

            buzCache.add(i, tempClient);
            if(i == 201){
                testClient = tempClient;
                testAddress = tempAddress;
            }
        }

        assertThat(buzCache.size(Client.class)).isEqualTo(500);

        assertThrows(PutInCacheException.class, () -> {
                        buzCache.add(501, new Client((long) 501, "Vasili_" + 501,
                new Address((long) 501, "AnyStreet"),
                List.of(new Phone(null, "13-555-22"), new Phone(null, "13-555-23"))));
        }) ;

        assertThat(buzCache.size(Client.class)).isEqualTo(500);

        assertThat(buzCache.get(201,Client.class).get()).isEqualTo(testClient);

        assertThat(buzCache.get("studentAddress",testAddress,Client.class).size()).isEqualTo(1);

        assertThat(buzCache.get("studentAddress",testAddress,Client.class).get(0)).isEqualTo(testClient);

        assertThat(buzCache.get("phoneList",phones,Client.class).size()).isEqualTo(500);

        buzCache.delete(201,Client.class);

        assertThat(buzCache.size(Client.class)).isEqualTo(499);

        assertThat(buzCache.get(201,Client.class).isPresent()).isEqualTo(false);

        buzCache.add(201,testClient);

        assertThat(buzCache.size(Client.class)).isEqualTo(500);

        assertThat(buzCache.get(201,Client.class).get()).isEqualTo(testClient);


        //check update and adding without direct id
        assert testClient != null;
        testClient.setName("Test");

        buzCache.add(testClient);

        assertThat(buzCache.get(201,Client.class).get()).isEqualTo(testClient);
    }


    private void makeTestDependencies() {
        var cfg = new Configuration();

        cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        cfg.setProperty("hibernate.connection.driver_class", "org.h2.Driver");

        cfg.setProperty("hibernate.connection.url", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");

        cfg.setProperty("hibernate.connection.username", "sa");
        cfg.setProperty("hibernate.connection.password", "");

        cfg.setProperty("hibernate.show_sql", "true");
        cfg.setProperty("hibernate.format_sql", "false");
        cfg.setProperty("hibernate.generate_statistics", "true");

        cfg.setProperty("hibernate.hbm2ddl.auto", "create");
        cfg.setProperty("hibernate.enable_lazy_load_no_trans", "false");

        serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(cfg.getProperties()).build();


        MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        metadataSources.addAnnotatedClass(Phone.class);
        metadataSources.addAnnotatedClass(Address.class);
        metadataSources.addAnnotatedClass(Client.class);
        metadata = metadataSources.getMetadataBuilder().build();
        sessionFactory = metadata.getSessionFactoryBuilder().build();
    }

    private void applyCustomSqlStatementLogger(SqlStatementLogger customSqlStatementLogger) {
        var jdbcServices = serviceRegistry.getService(JdbcServices.class);
        try {
            Field field = jdbcServices.getClass().getDeclaredField("sqlStatementLogger");
            field.setAccessible(true);
            field.set(jdbcServices, customSqlStatementLogger);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}