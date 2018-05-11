package ru.testtask.aventika.aventicatesttask;

public class BookResult {
    private String mTitle;
    private String mAuthors;
    private String mSmallThumnailLink;
    private String mSelfLink;

    public BookResult(){

    }

    public BookResult(String title, String authors, String smallThumnailLink, String selfLink) {
        mTitle = title;
        mAuthors = authors;
        mSmallThumnailLink = smallThumnailLink;
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

    public String getSmallThumnailLink() {
        return mSmallThumnailLink;
    }

    public void setSmallThumnailLink(String smallThumnailLink) {
        mSmallThumnailLink = smallThumnailLink;
    }

    public String getSelfLink() {
        return mSelfLink;
    }

    public void setSelfLink(String selfLink) {
        mSelfLink = selfLink;
    }
}
