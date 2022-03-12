package ru.buz.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.buz.crm.model.Address;
import ru.buz.crm.model.Client;
import ru.buz.crm.model.Phone;
import ru.buz.crm.service.DBServiceClient;

import java.util.*;


@Controller
public class ClientsController {
    DBServiceClient dbServiceClient;

    public ClientsController(DBServiceClient dbServiceClient) {
        this.dbServiceClient = dbServiceClient;
    }


    @GetMapping("/clients")
    public String clients(Model model){
        var clientList = dbServiceClient.findAll();
        model.addAttribute("clients",clientList);
//        var client = new Client(null, "Vasya", new Address(null, "AnyStreet"), Set.of(new Phone(null, "13-555-22"),new Phone(null, "13-555-23")));
//        dbServiceClient.saveClient(client);

        return "clients";
    }
}
