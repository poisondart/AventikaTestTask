package ru.testtask.aventika.aventicatesttask;

import android.net.Uri;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

/*Класс, который отвечает за получение списка найденных книг по запросу*/

public class BooksJSONFetcher implements JSONFetcher{
    private ArrayList<BookResult> mBookResults;

    public BooksJSONFetcher(){
        mBookResults = new ArrayList<>();
    }

    /*Небольшой метод для преобразования json-массива в красивую строку*/
    public static String JSONArrayToString(JSONArray jsonArray) throws JSONException{
        StringBuilder builder = new StringBuilder();
        for(int a =  0; a < jsonArray.length(); a++){
            builder.append(jsonArray.get(a).toString());
            if(a != jsonArray.length() - 1){
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    public ArrayList<BookResult> getBookResults() {
        return mBookResults;
    }
    @Override
    public String getJSON(String query){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONString = null;

        try{
            final String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
            final String QUERY_PARAM = "q"; // Parameter for the search string.
            final String MAX_RESULTS = "maxResults"; // Parameter that limits search results.
            final String PRINT_TYPE = "printType"; // Parameter to filter by print type.

            Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, query)
                    .appendQueryParameter(MAX_RESULTS, "15")
                    .appendQueryParameter(PRINT_TYPE, "books")
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
            JSONArray itemsArray = jsonObject.getJSONArray("items");

            int i = 0;
            String title = null;
            JSONArray authorsJSONArray;
            String authors = null;
            String selfLink = null;
            String smallThumbNail = null;
            while (i < itemsArray.length() || (authors == null && title == null)) {

                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                try {
                    title = volumeInfo.getString("title");
                    authorsJSONArray = volumeInfo.getJSONArray("authors");
                    authors = JSONArrayToString(authorsJSONArray);
                    smallThumbNail = imageLinks.getString("thumbnail");
                    selfLink = book.getString("selfLink");
                } catch (Exception e){
                    e.printStackTrace();
                }
                if (title != null && authors != null && selfLink != null && smallThumbNail != null){
                    BookResult result = new BookResult(title, authors, smallThumbNail, selfLink);
                    mBookResults.add(result);
                }
                i++;
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
