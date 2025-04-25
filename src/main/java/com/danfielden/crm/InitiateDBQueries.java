package com.danfielden.crm;

import java.util.ArrayList;

public class InitiateDBQueries {

    private static String createUserTable() {
        return "CREATE TABLE IF NOT EXISTS Users (" +
                "id INTEGER PRIMARY KEY NOT NULL, " +
                "username TEXT, " +
                "email TEXT, " +
                "hashed_pw TEXT, " +
                "salt TEXT)";
    }

    private static String createNotesTable() {
        return "CREATE TABLE IF NOT EXISTS Notes (" +
                "user_id INTEGER, " +
                "time_stamp TEXT, " +
                "note TEXT, " +
                "note_id TEXT PRIMARY KEY NOT NULL)";
    }

    public static ArrayList<String> createTableQueries() {
        ArrayList<String> queries = new ArrayList<>();
        queries.add(createUserTable());
        queries.add(createNotesTable());
        // TODO import Google collections library, and replace this with ImmutableList.copyOf(queries).
        // Also put ImmList<String> as the method return.
        return queries;
    }
}
