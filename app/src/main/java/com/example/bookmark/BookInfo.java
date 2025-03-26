package com.example.bookmark;

import java.util.ArrayList;

/**
 * BookInfo is a model class that represents the details of a book.
 * It contains various attributes such as title, author, publisher, description,
 * and links to preview, buy, or get more information about the book.
 */
public class BookInfo {

    // Book detail variables
    private String title;
    private String subtitle;
    private ArrayList<String> authors;
    private String publisher;
    private String publishedDate;
    private String description;
    private int pageCount;
    private String thumbnail;
    private String previewLink;
    private String infoLink;
    private String buyLink;

    /**
     * Constructor for the BookInfo class that initializes all book-related details.
     *
     * @param title         The title of the book.
     * @param subtitle      The subtitle of the book (if any).
     * @param authors       A list of authors who wrote the book.
     * @param publisher     The name of the publisher.
     * @param publishedDate The date the book was published.
     * @param description   A brief description of the book.
     * @param pageCount     The number of pages in the book.
     * @param thumbnail     A URL link to the book's thumbnail image.
     * @param previewLink   A URL link to preview the book.
     * @param infoLink      A URL link for additional book details.
     * @param buyLink       A URL link to purchase the book.
     */
    public BookInfo(String title, String subtitle, ArrayList<String> authors, String publisher,
                    String publishedDate, String description, int pageCount, String thumbnail,
                    String previewLink, String infoLink, String buyLink) {
        this.title = title;
        this.subtitle = subtitle;
        this.authors = authors;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
        this.pageCount = pageCount;
        this.thumbnail = thumbnail;
        this.previewLink = previewLink;
        this.infoLink = infoLink;
        this.buyLink = buyLink;
    }

    /**Book Details Getter and Setter Methods*/
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public ArrayList<String> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<String> authors) {
        this.authors = authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPreviewLink() {
        return previewLink;
    }

    public void setPreviewLink(String previewLink) {
        this.previewLink = previewLink;
    }

    public String getInfoLink() {
        return infoLink;
    }

    public void setInfoLink(String infoLink) {
        this.infoLink = infoLink;
    }

    public String getBuyLink() {
        return buyLink;
    }

    public void setBuyLink(String buyLink) {
        this.buyLink = buyLink;
    }
}

