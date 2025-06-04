package com.danfielden.crm;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Contact {
    private String firstName;
    private String middleNames;
    private String surname;
    private String title;
    private Birthday birthday;
    private String contactId;
    private byte[] image;

    private static final Gson gson = new Gson();

    public Contact() {
        super();
    }

    public Contact(String firstName, String middleNames, String surname, String title, Birthday birthday) {
        this.firstName = firstName;
        this.middleNames = middleNames;
        this.surname = surname;
        this.title = title;
        this.birthday = birthday;
    }

    public Contact(String firstName, String middleNames, String surname, String title, Birthday birthday, byte[] image) {
        this.firstName = firstName;
        this.middleNames = middleNames;
        this.surname = surname;
        this.title = title;
        this.birthday = birthday;
        this.image = image;
    }

    public Contact(String firstName, String middleNames, String surname, String title, Birthday birthday, String contactId) {
        this.firstName = firstName;
        this.middleNames = middleNames;
        this.surname = surname;
        this.title = title;
        this.birthday = birthday;
        this.contactId = contactId;
    }

    public Contact(String firstName, String middleNames, String surname, String title, Birthday birthday, String contactId, byte[] image) {
        this.firstName = firstName;
        this.middleNames = middleNames;
        this.surname = surname;
        this.title = title;
        this.birthday = birthday;
        this.contactId = contactId;
        this.image = image;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getMiddleNames() {
        return this.middleNames;
    }

    public String getSurname() {
        return this.surname;
    }

    public String getTitle() {
        return this.title;
    }

    public Birthday getBirthday() {
        return this.birthday;
    }

    public String getContactId() {
        return this.contactId;
    }

    public byte[] getImage() {
        return this.image;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: " + this.getTitle() + " " + this.getFirstName() + " " + this.getMiddleNames() + " " + this.getSurname());
        sb.append(System.lineSeparator());
        sb.append("Birthday: " + this.getBirthday());

        return sb.toString();
    }

    public JsonObject toJson() {
        JsonObject o = new JsonObject();
        o.addProperty("title", this.getTitle());
        o.addProperty("firstName", this.getFirstName());
        o.addProperty("middleNames", this.getMiddleNames());
        o.addProperty("surname", this.getSurname());
        o.addProperty("birthday", this.getBirthday().toString());

        return o;
    }
}
