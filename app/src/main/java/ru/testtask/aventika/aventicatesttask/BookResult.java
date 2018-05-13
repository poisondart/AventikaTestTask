package ru.testtask.aventika.aventicatesttask;

/*Класс модели для результатов запроса на поиск книги*/

public class BookResult {
    private String mTitle; //название книги
    private String mAuthors; // авторы книги
    private String mSmallThumbnailLink; //ссылка на маленькую картинку обложки
    private String mSelfLink; //ссылка на книгу

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
