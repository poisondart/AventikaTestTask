package ru.testtask.aventika.aventicatesttask;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private BookResultsAdapter mBookResultsAdapter;
    private ArrayList<BookResult> mBookResults;
    private String mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBookResults = new ArrayList<>();
        mToolbar = findViewById(R.id.toolbar_main);
        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);

        mSwipeRefreshLayout = findViewById(R.id.swipe);
        mRecyclerView = findViewById(R.id.books_list);
        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mBookResultsAdapter = new BookResultsAdapter(mBookResults);
        mRecyclerView.setAdapter(mBookResultsAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!mQuery.isEmpty()){
                    updateItems(mQuery);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_button, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!query.isEmpty()){
                    mQuery = query;
                    updateItems(mQuery);
                }
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    private void updateItems(String query){
        new FetchBookTask(query, MainActivity.this).execute();
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
            ArrayList<BookResult> bookResults = new ArrayList<>();
            MainActivity activity = mReference.get();
            activity.mSwipeRefreshLayout.setRefreshing(false);
            try{
                JSONObject jsonObject = new JSONObject(string);
                JSONArray itemsArray = jsonObject.getJSONArray("items");

                int i = 0;
                String title = null;
                String authors = null;
                String selfLink = null;
                String smallThumbNail = null;
                while (i < itemsArray.length() || (authors == null && title == null)) {

                    JSONObject book = itemsArray.getJSONObject(i);
                    JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                    try {
                        title = volumeInfo.getString("title");
                        authors = volumeInfo.getString("authors");
                        smallThumbNail = imageLinks.getString("thumbnail");
                        selfLink = book.getString("selfLink");
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    if (title != null && authors != null && selfLink != null && smallThumbNail != null){
                        BookResult result = new BookResult(title, authors, smallThumbNail, selfLink);
                        bookResults.add(result);
                    }
                    i++;
                }

            }catch (JSONException e){
                e.printStackTrace();
            }

            activity.mBookResultsAdapter.setBooksResults(bookResults);
            activity.mBookResultsAdapter.notifyDataSetChanged();
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
