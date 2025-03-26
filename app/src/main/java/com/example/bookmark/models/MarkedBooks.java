package com.example.bookmark.models;

public class MarkedBooks {
    private String title;
    private String author;
    private String publishedDate;
    private String thumbnailUrl;

    // Constructor
    public MarkedBooks(String title, String author, String publishedDate, String thumbnailUrl) {
        this.title = title;
        this.author = author;
        this.publishedDate = publishedDate;
        this.thumbnailUrl = thumbnailUrl;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getPublishedDate() {
        return publishedDate;
    }
    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
