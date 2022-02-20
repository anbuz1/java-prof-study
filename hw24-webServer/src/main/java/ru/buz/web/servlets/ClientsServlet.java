package ru.buz.web.servlets;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.buz.crm.model.Role;
import ru.buz.web.services.TemplateProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ClientsServlet extends HttpServlet {

    private static final String GUEST_PAGE_TEMPLATE = "clients.html";
    private static final String ADMIN_PAGE_TEMPLATE = "clients-admin.html";

    private final TemplateProcessor templateProcessor;

    public ClientsServlet(TemplateProcessor templateProcessor) {
        this.templateProcessor = templateProcessor;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
        Map<String, Object> paramsMap = new HashMap<>();
        var role = (Role) req.getSession(false).getAttribute("role");
        response.setContentType("text/html");

        response.getWriter().println(templateProcessor.getPage(
                 getFilteredTemplate(role), paramsMap));
    }

    private  String getFilteredTemplate(Role role){
        var userRole = role.getRole();
        switch (userRole) {
            case "admin" -> {
                return ADMIN_PAGE_TEMPLATE;
            }
            case "guest" -> {
                return GUEST_PAGE_TEMPLATE;
            }
        }
        return "";
    }

}
