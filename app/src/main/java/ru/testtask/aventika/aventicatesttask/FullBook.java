package ru.testtask.aventika.aventicatesttask;

/*Класс модели для выведения более подробной информации о книге*/

public class FullBook {
    private String mPublisher, //издатель книги
            mPublishDate,  //дата издания книги
            mDesc, //описание книги
            mPageCount, //кол-во страниц
            mCategory, //категория
            mImageLink; //ссылка на большую картинку обожки книги

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
