package com.danfielden.crm;

import javax.servlet.http.Part;
import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Random;

public class CrmAppDB {
    private final Connection connect;

    public CrmAppDB() throws Exception {
        connect = DriverManager.getConnection("jdbc:sqlite:crmapp.db", "root", "");

        // Initiate db tables if they do not exist
        initiateTables();
    }

    public synchronized HashMap<String, Note> getNotes(long userId) throws Exception {
        String query = "SELECT * FROM Notes WHERE user_id = ?";
        HashMap<String, Note> notes = new HashMap<>();
        try (PreparedStatement stmt = connect.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String uid = rs.getString("note_id");
                String note = rs.getString("note");
                String timeStamp = rs.getString("time_stamp");
                notes.put(uid, new Note(note, userId, timeStamp, uid));
            }
            return notes;
        }
    }

    public synchronized HashMap<String, Note> getContactNotes(long userId, String contactId) throws Exception {
        System.out.println(contactId);
        String query = "SELECT * FROM Notes WHERE user_id = ? AND note LIKE ?";
        HashMap<String, Note> notes = new HashMap<>();
        try (PreparedStatement stmt = connect.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, userId);
            stmt.setString(2, "%" + contactId + "%");

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String uid = rs.getString("note_id");
                String note = rs.getString("note");
                String timeStamp = rs.getString("time_stamp");
                notes.put(uid, new Note(note, userId, timeStamp, uid));
            }
            return notes;
        }
    }

    public synchronized String addNewNote(long userId, long timestamp, String note) throws Exception {
        String query = "INSERT INTO Notes (user_id, time_stamp, note, note_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            String uid = generateUID();
            stmt.setLong(1,userId);
            stmt.setLong(2, timestamp);
            stmt.setString(3, note);
            stmt.setString(4, uid);

            stmt.executeUpdate();
            return uid;
        }
    }

    public synchronized Contact getContactFromId(String contactId) throws Exception {
        String query = "SELECT * FROM Contacts WHERE contact_id = ?";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setString(1, contactId);

            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                throw new IllegalStateException("Unable to find contact with id " + contactId);
            } else {
                String firstName = rs.getString("first_name");
                String middleNames = rs.getString("middle_names");
                String surname = rs.getString("surname");
                String title = rs.getString("title");
                int day = rs.getInt("d");
                int month = rs.getInt("m");
                int year = rs.getInt("y");
                byte[] imageBytes = rs.getBytes("image");

                Contact c = new Contact(firstName, middleNames, surname, title, new Birthday(day, month, year), contactId, imageBytes);
                System.out.println(c);
                return c;
            }
        }
    }

    public synchronized HashMap<String, Contact> getContacts(long userId) throws Exception {
        String query = "SELECT * FROM Contacts WHERE user_id = ?";
        HashMap<String, Contact> contacts = new HashMap<>();
        try (PreparedStatement stmt = connect.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String firstName = rs.getString("first_name");
                String middleNames = rs.getString("middle_names");
                String surname = rs.getString("surname");
                String title = rs.getString("title");
                int day = rs.getInt("d");
                int month = rs.getInt("m");
                int year = rs.getInt("y");
                String contactId = rs.getString("contact_id");
                byte[] imageBytes = rs.getBytes("image");


                contacts.put(contactId, new Contact(firstName, middleNames, surname, title, new Birthday(day, month, year), contactId, imageBytes));
            }
            return contacts;
        }
    }

    public synchronized String addNewContact(Contact contact, long userId) throws Exception {
        String query = "INSERT INTO Contacts (first_name, middle_names, surname, title, d, m, y, image, user_id, contact_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        //String imagePath = "src/main/resources/static/images/danny.jpg";


        try (PreparedStatement stmt = connect.prepareStatement(query)) {

            String contactId = generateUID();
            stmt.setString(1, contact.getFirstName());
            stmt.setString(2, contact.getMiddleNames());
            stmt.setString(3, contact.getSurname());
            stmt.setString(4, contact.getTitle());
            stmt.setInt(5, contact.getBirthday().getDay());
            stmt.setInt(6, contact.getBirthday().getMonth());
            stmt.setInt(7, contact.getBirthday().getYear());
            stmt.setBytes(8, contact.getImage());
            stmt.setLong(9, userId);
            stmt.setString(10, contactId);


            stmt.executeUpdate();
            return contactId;
        }
    }

    public synchronized String updateContact(Contact contact, String contactId, long userId) throws Exception {
        String query = "UPDATE Contacts SET first_name=?, middle_names=?, surname=?, title=?, d=?, m=?, y=?, image=?, user_id=?" +
                "WHERE contact_id=?";

        try (PreparedStatement stmt = connect.prepareStatement(query)) {

            stmt.setString(1, contact.getFirstName());
            stmt.setString(2, contact.getMiddleNames());
            stmt.setString(3, contact.getSurname());
            stmt.setString(4, contact.getTitle());
            stmt.setInt(5, contact.getBirthday().getDay());
            stmt.setInt(6, contact.getBirthday().getMonth());
            stmt.setInt(7, contact.getBirthday().getYear());
            stmt.setBytes(8, contact.getImage());
            stmt.setLong(9, userId);
            stmt.setString(10, contactId);


            stmt.executeUpdate();
            return "done";
        }
    }

    public synchronized String updateContactNoImage(Contact contact, String contactId, long userId) throws Exception {
        String query = "UPDATE Contacts SET first_name=?, middle_names=?, surname=?, title=?, d=?, m=?, y=?, user_id=?" +
                "WHERE contact_id=?";

        try (PreparedStatement stmt = connect.prepareStatement(query)) {

            stmt.setString(1, contact.getFirstName());
            stmt.setString(2, contact.getMiddleNames());
            stmt.setString(3, contact.getSurname());
            stmt.setString(4, contact.getTitle());
            stmt.setInt(5, contact.getBirthday().getDay());
            stmt.setInt(6, contact.getBirthday().getMonth());
            stmt.setInt(7, contact.getBirthday().getYear());
            stmt.setLong(8, userId);
            stmt.setString(9, contactId);


            stmt.executeUpdate();
            return "done";
        }
    }

    public synchronized void deleteNote(String noteId) throws Exception {
        String query = "DELETE FROM Notes WHERE note_id = ?";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setString(1, noteId);
            stmt.executeUpdate();
        }
    }

    // PRIVATE

    private synchronized void initiateTables() throws Exception {
        ArrayList<String> queries = InitiateDBQueries.createTableQueries();

        for (String q : queries) {
            connect.createStatement().execute(q);
        }
    }

    private static String generateUID() {
        Random rand = new Random();
        return String.format("%x%x", rand.nextLong(), rand.nextLong());
    }

}
