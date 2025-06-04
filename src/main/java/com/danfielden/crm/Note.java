package com.danfielden.crm;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public final class Note implements Comparable<Note> {
    private final String noteContent;
    private final long authorId;
    private final String timeStamp;
    private final String uid;
    private static final Gson gson = new Gson();


    public Note(String noteContent, long authorId, String timeStamp, String uid) {
        this.noteContent = noteContent;
        this.authorId = authorId;
        this.timeStamp = timeStamp;
        this.uid = uid;
    }

    public String getNoteContent() {
        return this.noteContent;
    }

    public long getAuthorId() {
        return this.authorId;
    }

    public String getTimeStamp() {
        return this.timeStamp;
    }

    public String getUid() {
        return this.uid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getNoteContent());
        sb.append( "(");
        sb.append(this.getTimeStamp());
        sb.append( ")");

        return sb.toString();
    }

    @Override
    public int compareTo(Note n) {
        return this.getTimeStamp().compareTo(n.getTimeStamp());
    }

    public JsonObject toJson() {
        JsonObject o = new JsonObject();
        o.addProperty("noteContent", this.getNoteContent());
        o.addProperty("authorId", this.getAuthorId());
        o.addProperty("timeStamp", this.getTimeStamp());
        o.addProperty("uid", this.getUid());

        return o;
    }


}
