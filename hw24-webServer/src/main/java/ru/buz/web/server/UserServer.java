package ru.buz.web.server;

import com.google.gson.Gson;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.buz.dao.ClientDao;
import ru.buz.dao.UserDao;
import ru.buz.web.helpers.FileSystemHelper;
import ru.buz.web.services.TemplateProcessor;
import ru.buz.web.services.UserAuthService;
import ru.buz.web.servlets.AuthorizationFilter;
import ru.buz.web.servlets.ClientsServlet;
import ru.buz.web.servlets.LoginServlet;
import ru.buz.web.servlets.UsersApiServlet;

import java.util.Arrays;

public class UserServer {

    private static final String START_PAGE_NAME = "index.html";
    private static final String COMMON_RESOURCES_DIR = "static";

    private final ClientDao clientDao;
    private final Gson gson;
    private final UserAuthService authService;
    protected final TemplateProcessor templateProcessor;
    private final Server server;


    public UserServer(int port,  ClientDao clientDao,Gson gson, TemplateProcessor templateProcessor, UserAuthService userAuthService) {
        this.clientDao = clientDao;
        this.gson = gson;
        this.templateProcessor = templateProcessor;
        this.server = new Server(port);
        this.authService = userAuthService;
    }
    public void start() throws Exception {
        if (server.getHandlers().length == 0) {
            initContext();
        }
        server.start();
    }


    public void join() throws Exception {
        server.join();
    }


    public void stop() throws Exception {
        server.stop();
    }

    private Server initContext() {

        ResourceHandler resourceHandler = createResourceHandler();
        ServletContextHandler servletContextHandler = createServletContextHandler();

        HandlerList handlers = new HandlerList();
        handlers.addHandler(resourceHandler);
        handlers.addHandler(applySecurity(servletContextHandler, "/clients", "/api/user/*"));

        server.setHandler(handlers);
        return server;
    }

    protected Handler applySecurity(ServletContextHandler servletContextHandler, String... paths) {
        servletContextHandler.addServlet(new ServletHolder(new LoginServlet(templateProcessor, authService)), "/login");
        AuthorizationFilter authorizationFilter = new AuthorizationFilter();
        Arrays.stream(paths).forEachOrdered(path -> servletContextHandler.addFilter(new FilterHolder(authorizationFilter), path, null));
        return servletContextHandler;
    }
    private ResourceHandler createResourceHandler() {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(false);
        resourceHandler.setWelcomeFiles(new String[]{START_PAGE_NAME});
        resourceHandler.setResourceBase(FileSystemHelper.localFileNameOrResourceNameToFullPath(COMMON_RESOURCES_DIR));
        return resourceHandler;
    }

    private ServletContextHandler createServletContextHandler() {
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.addServlet(new ServletHolder(new ClientsServlet(templateProcessor)), "/clients");
        servletContextHandler.addServlet(new ServletHolder(new UsersApiServlet(clientDao, gson)), "/api/user/*");
        return servletContextHandler;
    }



}
