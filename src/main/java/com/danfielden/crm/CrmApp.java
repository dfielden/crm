package com.danfielden.crm;

import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;


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

    @GetMapping("/allnotes")
    public String allNotes(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        setHeaders(resp);
        return "notes";
    }

    @ResponseBody
    @GetMapping("/getnotes")
    public String getNotes(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String result = gson.toJson(db.getNotes(1));
        return result;
    }

    @GetMapping("/notes/{contactId}")
    public String contactNotes(@PathVariable(value="contactId") String contactId, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        return "personalnotes";

    }

    @ResponseBody
    @GetMapping("/getnotes/{contactId}")
    public String getContactNotes(@PathVariable(value="contactId") String contactId, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        System.out.println(contactId);
        String result = gson.toJson(db.getContactNotes(1, contactId));
        System.out.println(result);
        return result;
    }

    @ResponseBody
    @PostMapping("/submitnewnote")
    public String submitNewNote(@RequestBody String note, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        try {
            // returns the uid of newly-added note
            System.out.println(note);
            return db.addNewNote(1, System.currentTimeMillis(), note);
        } catch (Exception e) {
            return gson.toJson(e.getMessage());
        }
    }

    @ResponseBody
    @GetMapping("/getcontacts")
    public String getContacts(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String result = gson.toJson(db.getContacts(1));
        return result;
    }

    @ResponseBody
    @GetMapping("/getcontact/{id}")
    public String getContact(@PathVariable(value="id") String id, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        return gson.toJson(db.getContactFromId(id));
    }

    @ResponseBody
    @PostMapping("/submitnewcontact")
    public String submitNewContact(@RequestBody Contact contact, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        System.out.println(contact);
        try {
            // returns the uid of newly-added note
            return db.addNewContact(contact, 1);
        } catch (Exception e) {
            return gson.toJson(e.getMessage());
        }
    }

    @ResponseBody
    @PostMapping(value="/updatecontact/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String updateWorkout(@RequestBody Contact contact, @PathVariable(value="id") String contactId, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        // TODO: link to user id
        //long userId = getUserId(req, resp);
        //System.out.println("Image: " + Arrays.toString(contact.getImage()));
        if (contact.getImage().length == 0) {
            db.updateContactNoImage(contact, contactId, 1);
        } else {
            db.updateContact(contact, contactId, 1);
        }
        return gson.toJson("Success");
    }

    @ResponseBody
    @PostMapping(value="/deletenote/{noteId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String createWorkout(@PathVariable(value="noteId") String noteId, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        db.deleteNote(noteId);
        return gson.toJson("SUCCESS");
    }

    private void setHeaders(HttpServletResponse resp) {
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    }
}
