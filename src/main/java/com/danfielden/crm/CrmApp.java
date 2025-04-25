package com.danfielden.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@SpringBootApplication
@Controller
public class CrmApp {


    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CrmApp.class);
        app.run(args);
    }

    @GetMapping("/")
    public String home(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        setHeaders(resp);
        return "index";
    }

    @GetMapping("/contacts")
    public String contacts(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        setHeaders(resp);
        return "contacts";
    }

    @GetMapping("/groups")
    public String groups(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        setHeaders(resp);
        return "groups";
    }

    @GetMapping("/calendar")
    public String calendar(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        setHeaders(resp);
        return "calendar";
    }

    @ResponseBody
    @PostMapping("/submitnewnote")
    public String submitNewNote(@RequestBody String note, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        System.out.println("Submitted new note");
        return "";
    }

    private void setHeaders(HttpServletResponse resp) {
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    }
}
