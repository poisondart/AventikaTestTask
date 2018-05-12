package ru.testtask.aventika.aventicatesttask;

public class FullBook {
    private String mTitle, mAuthor, mPublisher,
            mPublishDate, mDesc, mPageCount, mCategory, mImageLink;

    public FullBook() {
    }

    public FullBook(String title, String author, String publisher, String publishDate,
                    String desc, String pageCount, String category, String imageLink) {
        mTitle = title;
        mAuthor = author;
        mPublisher = publisher;
        mPublishDate = publishDate;
        mDesc = desc;
        mPageCount = pageCount;
        mCategory = category;
        mImageLink = imageLink;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getPublisher() {
        return mPublisher;
    }

    public void setPublisher(String publisher) {
        mPublisher = publisher;
    }

    public String getPublishDate() {
        return mPublishDate;
    }

    public void setPublishDate(String publishDate) {
        mPublishDate = publishDate;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String desc) {
        mDesc = desc;
    }

    public String getPageCount() {
        return mPageCount;
    }

    public void setPageCount(String pageCount) {
        mPageCount = pageCount;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public String getImageLink() {
        return mImageLink;
    }

    public void setImageLink(String imageLink) {
        mImageLink = imageLink;
    }
}
