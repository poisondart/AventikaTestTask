package ru.testtask.aventika.aventicatesttask;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mBookInput;
    private TextView mTitleText;
    private TextView mAuthorText;
    private Button mGoButton;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar_main);
        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);

        mBookInput = findViewById(R.id.edit_query);
        mTitleText = findViewById(R.id.titleText);
        mAuthorText = findViewById(R.id.authorText);
        mGoButton = findViewById(R.id.go);
        mSwipeRefreshLayout = findViewById(R.id.swipe);

        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = mBookInput.getText().toString();
                if(!query.isEmpty()){
                    new FetchBookTask(query, MainActivity.this).execute();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_button, menu);
        return true;
    }

    private static class FetchBookTask extends AsyncTask<Void, Void, String>{
        private String mQueryString;
        private WeakReference<MainActivity> mReference;
        public FetchBookTask(String query, MainActivity reference) {
            mQueryString = query;
            mReference = new WeakReference<>(reference);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity activity = mReference.get();
            activity.mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            MainActivity activity = mReference.get();
            activity.mSwipeRefreshLayout.setRefreshing(false);
            try{
                JSONObject jsonObject = new JSONObject(string);
                JSONArray itemsArray = jsonObject.getJSONArray("items");

                int i = 0;
                String title = null;
                String authors = null;
                while (i < itemsArray.length() || (authors == null && title == null)) {

                    JSONObject book = itemsArray.getJSONObject(i);
                    JSONObject volumeInfo = book.getJSONObject("volumeInfo");


                    try {
                        title = volumeInfo.getString("title");
                        authors = volumeInfo.getString("authors");
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    i++;
                }

                if (title != null && authors != null){
                    activity.mTitleText.setText(title);
                    activity.mAuthorText.setText(authors);
                    activity.mBookInput.setText("");
                } else {

                    activity.mTitleText.setText("No sir");
                    activity.mAuthorText.setText("No sir");
                }

            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String bookJSONString = null;

            try{
                final String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
                final String QUERY_PARAM = "q"; // Parameter for the search string.
                final String MAX_RESULTS = "maxResults"; // Parameter that limits search results.
                final String PRINT_TYPE = "printType"; // Parameter to filter by print type.

                Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, mQueryString)
                        .appendQueryParameter(MAX_RESULTS, "10")
                        .appendQueryParameter(PRINT_TYPE, "books")
                        .build();
                URL requestURL = new URL(builtURI.toString());

                urlConnection = (HttpURLConnection) requestURL.openConnection();
                urlConnection.setRequestMethod("GET");
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
    }
}
