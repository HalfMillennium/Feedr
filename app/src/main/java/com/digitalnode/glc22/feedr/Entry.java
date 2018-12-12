package com.digitalnode.glc22.feedr;

public class Entry implements EntryInterface {
    private String source = null;
    private String title = null;
    private String author = null;
    private String dateCreated = null;
    private String thumbnail = null;
    private String textContent = null;
    private String label = null;

    /*** for the sake of testing, an entry will only consist of source, title, and dateCreated ***/
    public Entry(String source, String title, String author, String thumbnail, String dateCreated, String textContent, String label) {
        this.source = source;
        this.title = title;
        this.author = author;
        this.dateCreated = dateCreated;
        this.thumbnail = thumbnail;
        this.textContent = textContent;
        this.label = label;
    }

    public String getAuthor() {
        return author;
    }

    public String getSource() {
        return source;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getTitle() {
        return title;
    }

    public String getThumbnail() { return thumbnail; }

    public String getTextContent() { return textContent; }

    public String getLabel() {
        return label;
    }
}
