package ru.testtask.aventika.aventicatesttask;

import android.net.Uri;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

public class FullBookJSONFetcher implements JSONFetcher{
    private FullBook mFullBook;

    public FullBookJSONFetcher(){
        mFullBook = new FullBook();
    }

    public FullBook getFullBook() {
        return mFullBook;
    }

    @Override
    public String getJSON(String query) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONString = null;

        try{
            Uri builtURI = Uri.parse(query).buildUpon()
                    .build();
            URL requestURL = new URL(builtURI.toString());

            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(5000);
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            StringBuilder builder = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }
            if (builder.length() == 0) {
                return null;
            }
            bookJSONString = builder.toString();
        }catch (SocketTimeoutException se){
            se.printStackTrace();
            return null;
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bookJSONString;
    }

    @Override
    public void parseResults(String jsonString) {
        try{
            JSONObject jsonObject = new JSONObject(jsonString);
            String publisher = null;
            String publishDate = null;
            String desc = null;
            String pages = null;
            String category = null;
            String largeThumbnail = null;
            JSONObject volumeInfo = jsonObject.getJSONObject("volumeInfo");
            JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
            try {
                publisher = volumeInfo.getString("publisher");
                publishDate = volumeInfo.getString("publishedDate");
                desc = volumeInfo.getString("description");
                pages = volumeInfo.getString("pageCount");
                category = volumeInfo.getString("categories");
                largeThumbnail = imageLinks.getString("medium");
            } catch (Exception e){
                e.printStackTrace();
            }

            if(publisher != null) mFullBook.setPublisher(publisher);
            else mFullBook.setPublisher("???");

            if (publishDate != null) mFullBook.setPublishDate(publishDate);
            else mFullBook.setPublishDate("???");

            if (desc != null) mFullBook.setDesc(desc);
            else mFullBook.setDesc("???");

            if (pages != null) mFullBook.setPageCount(pages);
            else mFullBook.setPageCount("???");

            if (category != null) mFullBook.setCategory(category);
            else mFullBook.setCategory("???");

            if (largeThumbnail != null) mFullBook.setImageLink(largeThumbnail);
            else mFullBook.setImageLink("");

        }catch (JSONException e){
            e.printStackTrace();
        }

    }
}
