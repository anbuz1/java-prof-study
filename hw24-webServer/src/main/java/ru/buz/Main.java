package ru.buz;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.buz.core.repository.DataTemplateHibernate;
import ru.buz.core.repository.HibernateUtils;
import ru.buz.core.sessionmanager.TransactionManagerHibernate;
import ru.buz.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.buz.crm.model.*;
import ru.buz.dao.ClientDao;
import ru.buz.dao.ClientDaoImpl;
import ru.buz.dao.UserDao;
import ru.buz.dao.UserDaoImpl;
import ru.buz.web.server.UserServer;
import ru.buz.web.services.TemplateProcessor;
import ru.buz.web.services.TemplateProcessorImpl;
import ru.buz.web.services.UserAuthService;
import ru.buz.web.services.UserAuthServiceImpl;

public class Main {
    private static final int WEB_SERVER_PORT = 9090;
    private static final String TEMPLATES_DIR = "/templates/";
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";


    public static void main(String[] args) throws Exception {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");
        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();
        var sessionFactory = HibernateUtils.buildSessionFactory(configuration,User.class, Role.class, Client.class, Address.class, Phone.class);
        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        var userTemplate = new DataTemplateHibernate<>(User.class);
        var clientTemplate = new DataTemplateHibernate<>(Client.class);


        UserDao userDao = new UserDaoImpl(userTemplate,transactionManager);
        ClientDao clientDao = new ClientDaoImpl(clientTemplate,transactionManager);
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        TemplateProcessor templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);
        UserAuthService userAuthService = new UserAuthServiceImpl(userDao);

        UserServer usersWebServer = new UserServer(WEB_SERVER_PORT, clientDao,
                gson, templateProcessor, userAuthService);

        usersWebServer.start();
        usersWebServer.join();

    }
}
