package com.danfielden.crm;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CrmAppDB {
    private final Connection connect;

    public CrmAppDB() throws Exception {
        connect = DriverManager.getConnection("jdbc:sqlite:crmapp.db", "root", "");

        // Initiate db tables if they do not exist
        initiateTables();
    }

    public synchronized HashMap<String, String> getNotes(long userId) throws Exception {
        String query = "SELECT * FROM Notes WHERE user_id = ?";
        HashMap<String, String> notes = new HashMap<>();
        try (PreparedStatement stmt = connect.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String uid = rs.getString("note_id");
                String note = rs.getString("note");
                notes.put(uid, note);
            }
            return notes;
        }
    }

    public synchronized String addNewNote(long userId, long timestamp, String note) throws Exception {
        String query = "INSERT INTO Notes (user_id, time_stamp, note, note_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setLong(1,userId);
            stmt.setLong(2, timestamp);
            stmt.setString(3, note);
            stmt.setString(4, generateUID());

            stmt.executeUpdate();
            return note;
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
