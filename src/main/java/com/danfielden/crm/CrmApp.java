package com.danfielden.crm;

import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@SpringBootApplication
@Controller
public class CrmApp {
    private final CrmAppDB db;
    private static final Gson gson = new Gson();


    public CrmApp() throws Exception {
        db = new CrmAppDB();
    }


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
    @GetMapping("/getnotes")
    public String getNotes(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String result = gson.toJson(db.getNotes(1));
        return result;
    }

    @ResponseBody
    @PostMapping("/submitnewnote")
    public String submitNewNote(@RequestBody String note, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        System.out.println(note);
        try {
            db.addNewNote(1, System.currentTimeMillis(), note);
        } catch (Exception e) {
            return gson.toJson(e.getMessage());
        }
        return gson.toJson("Received message");
    }

    private void setHeaders(HttpServletResponse resp) {
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    }
}
