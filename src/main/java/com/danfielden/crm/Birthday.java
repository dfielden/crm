package com.danfielden.crm;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Birthday {
    private final int day;
    private final int month;
    private final int year;

    private static final Gson gson = new Gson();


    public Birthday(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getDay() {
        return this.day;
    }

    public int getMonth() {
        return this.month;
    }

    public int getYear() {
        return this.year;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getDay() + " " + this.getMonth() + " " + this.getYear());

        return sb.toString();
    }

    public JsonObject toJson() {
        JsonObject o = new JsonObject();
        o.addProperty("day", this.getDay());
        o.addProperty("month", this.getMonth());
        o.addProperty("year", this.getYear());

        return o;
    }
}
