package ru.buz.web.servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.buz.crm.model.Client;
import ru.buz.dao.ClientDao;

import java.io.IOException;
import java.util.stream.Collectors;


public class UsersApiServlet extends HttpServlet {

    private static final int ID_PATH_PARAM_POSITION = 1;

    private final ClientDao clientDao;
    private final Gson gson;

    public UsersApiServlet(ClientDao clientDao, Gson gson) {
        this.clientDao = clientDao;
        this.gson = gson;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Client user = clientDao.findById(extractIdFromRequest(request)).orElse(null);

        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();
        out.print(gson.toJson(user));
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String bodyJSON = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

        Client client = gson.fromJson(bodyJSON, Client.class);
        setNullIdIfNeed(client);
        Client clientResult = clientDao.createOrUpdate(client).orElseThrow();

        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();
        out.print(gson.toJson(clientResult));
    }

    private long extractIdFromRequest(HttpServletRequest request) {
        String[] path = request.getPathInfo().split("/");
        String id = (path.length > 1)? path[ID_PATH_PARAM_POSITION]: String.valueOf(- 1);
        return Long.parseLong(id);
    }
    private void setNullIdIfNeed(Client client){
        long clientId = client.getId();
        if(clientId == 0){
            client.setId(null);
        }
    }

}
