package ru.testtask.aventika.aventicatesttask;

import android.content.Context;
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
import java.lang.ref.WeakReference;
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
        initUI();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!mQuery.isEmpty()){
                    updateItems(mQuery);
                }
            }
        });
    }

    private void initUI(){
        mToolbar = findViewById(R.id.toolbar_main);
        mToolbar.setTitle(R.string.searchActivityTitle);
        setSupportActionBar(mToolbar);

        mSwipeRefreshLayout = findViewById(R.id.swipe);
        mRecyclerView = findViewById(R.id.books_list);
        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mBookResultsAdapter = new BookResultsAdapter(mBookResults);
        mRecyclerView.setAdapter(mBookResultsAdapter);
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
        private BooksJSONFetcher mBooksJSONFetcher;
        public FetchBookTask(String query, MainActivity reference) {
            mQueryString = query;
            mReference = new WeakReference<>(reference);
            mBooksJSONFetcher = new BooksJSONFetcher();
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
            mBooksJSONFetcher.parseResults(string);
            activity.mBookResultsAdapter.setBooksResults(mBooksJSONFetcher.getBookResults());
            activity.mBookResultsAdapter.notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(Void... voids) {
            return mBooksJSONFetcher.getJSON(mQueryString);
        }
    }
}
