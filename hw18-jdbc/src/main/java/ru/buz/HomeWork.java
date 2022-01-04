package ru.buz;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Container;
import ru.buz.core.repository.executor.DbExecutorImpl;
import ru.buz.core.sessionmanager.TransactionRunnerJdbc;
import ru.buz.crm.datasource.DriverManagerDataSource;
import ru.buz.crm.model.Client;
import ru.buz.crm.model.Manager;
import ru.buz.crm.service.DbServiceClientImpl;
import ru.buz.crm.service.DbServiceManagerImpl;
import ru.buz.jdbc.mapper.*;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;
import java.util.Properties;
import org.testcontainers.containers.PostgreSQLContainer;

public class HomeWork {
    @Container
    private final static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:12-alpine");

    private static final Logger log = LoggerFactory.getLogger(HomeWork.class);

    public static void main(String[] args) {

        var properties = getAppProperties();

        postgresqlContainer
                .withDatabaseName("testBD")
                .withUsername(properties.getProperty("username"))
                .withPassword(decryptPass(properties.getProperty("password")));
        postgresqlContainer.start();
// Общая часть
        var dataSource = new DriverManagerDataSource(
                postgresqlContainer.getJdbcUrl(),
                properties.getProperty("username"),
                decryptPass(properties.getProperty("password")));
        flywayMigrations(dataSource);
        var transactionRunner = new TransactionRunnerJdbc(dataSource);
        var dbExecutor = new DbExecutorImpl();

// Работа с клиентом
        EntityClassMetaData<Client> entityClassMetaDataClient  = new EntityClassMetaDataImpl<>(Client.class);
        EntitySQLMetaData entitySQLMetaDataClient = new EntitySQLMetaDataImpl(entityClassMetaDataClient);
        var dataTemplateClient = new DataTemplateJdbc<>(dbExecutor, entitySQLMetaDataClient,entityClassMetaDataClient); //реализация DataTemplate, универсальная

// Код дальше должен остаться
        var dbServiceClient = new DbServiceClientImpl(transactionRunner, dataTemplateClient);
        dbServiceClient.saveClient(new Client("dbServiceFirst"));
        dbServiceClient.saveClient(new Client(1L,"dbServiceFirstTest"));

        var clientSecond = dbServiceClient.saveClient(new Client("dbServiceSecond"));
        var clientSecondSelected = dbServiceClient.getClient(clientSecond.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecond.getId()));
        log.info("clientSecondSelected:{}", clientSecondSelected);

// Сделайте тоже самое с классом Manager (для него надо сделать свою таблицу)

        EntityClassMetaData<Manager> entityClassMetaDataManager = new EntityClassMetaDataImpl<>(Manager.class);
        EntitySQLMetaData entitySQLMetaDataManager = new EntitySQLMetaDataImpl(entityClassMetaDataManager);
        var dataTemplateManager = new DataTemplateJdbc<>(dbExecutor, entitySQLMetaDataManager,entityClassMetaDataManager);

        var dbServiceManager = new DbServiceManagerImpl(transactionRunner, dataTemplateManager);
        dbServiceManager.saveManager(new Manager("ManagerFirst"));

        var managerSecond = dbServiceManager.saveManager(new Manager("ManagerSecond"));
        var managerSecondSelected = dbServiceManager.getManager(managerSecond.getNo())
                .orElseThrow(() -> new RuntimeException("Manager not found, id:" + managerSecond.getNo()));
        log.info("managerSecondSelected:{}", managerSecondSelected);
        dbServiceManager.findAll();
        postgresqlContainer.stop();
    }

    private static void flywayMigrations(DataSource dataSource) {
        log.info("db migration started...");
        var flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:/")
                .load();
        flyway.migrate();
        log.info("db migration finished.");
        log.info("***");
    }

    private static Properties getAppProperties() {
        String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("properties/")).getPath();
        String appConfigPath = rootPath + "connection.properties";
        Properties appProperties = new Properties();
        try {
            appProperties.load(new FileInputStream(appConfigPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return appProperties;
    }

    private static String decryptPass(String pass) {
        byte[] plainText = Base64.getDecoder()
                .decode(pass);
        return new String(plainText);

    }
}
