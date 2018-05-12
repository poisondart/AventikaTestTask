package ru.testtask.aventika.aventicatesttask;

public class FullBook {
    private String mPublisher,
            mPublishDate, mDesc, mPageCount, mCategory, mImageLink;

    public FullBook() {
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
