package ru.testtask.aventika.aventicatesttask;

public class BookResult {
    private String mTitle;
    private String mAuthors;
    private String mSmallThumbnailLink;
    private String mSelfLink;

    public BookResult(){

    }

    public BookResult(String title, String authors, String smallThumbnailLink, String selfLink) {
        mTitle = title;
        mAuthors = authors;
        mSmallThumbnailLink = smallThumbnailLink;
        mSelfLink = selfLink;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getAuthors() {
        return mAuthors;
    }

    public void setAuthors(String authors) {
        mAuthors = authors;
    }

    public String getSmallThumbnailLink() {
        return mSmallThumbnailLink;
    }

    public void setSmallThumbnailLink(String smallThumbnailLink) {
        mSmallThumbnailLink = smallThumbnailLink;
    }

    public String getSelfLink() {
        return mSelfLink;
    }

    public void setSelfLink(String selfLink) {
        mSelfLink = selfLink;
    }

}
