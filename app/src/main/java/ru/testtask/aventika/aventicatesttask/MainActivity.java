package ru.testtask.aventika.aventicatesttask;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/*Главная активность приложения, в ней происходит поиск книг*/

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private LinearLayout mSearchTipView;
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

    /*Метод для инициализации виджетов интерфейса*/
    private void initUI(){
        mToolbar = findViewById(R.id.toolbar_main);
        mToolbar.setTitle(R.string.searchActivityTitle);
        setSupportActionBar(mToolbar);

        mSwipeRefreshLayout = findViewById(R.id.swipe);
        mRecyclerView = findViewById(R.id.books_list);
        mSearchTipView = findViewById(R.id.search_tip_view);
        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mBookResultsAdapter = new BookResultsAdapter(mBookResults);
        mRecyclerView.setAdapter(mBookResultsAdapter);
    }

    /*Обработка событий виджета поиска*/
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
        /*при повторном нажатии на кнопку удаления текста (крестик) происходит удаление результатов поиска*/
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mBookResultsAdapter.clearBooks();
                mBookResultsAdapter.notifyDataSetChanged();
                return true;
            }
        });

        return true;
    }
    /*Метод для запуска службы поиска*/
    private void updateItems(String query){
        if(isOnline()){
            new FetchBookTask(query, MainActivity.this).execute();
        }else{
            Toast.makeText(getApplicationContext(), R.string.no_network, Toast.LENGTH_LONG).show();
        }

    }
    /*Метод для проверки наличия подключения к интернету*/
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    /*AsyncTask-класс для получения результатов поиска*/
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
            activity.mSearchTipView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            MainActivity activity = mReference.get();
            if(string == null){
                Toast.makeText(activity, R.string.no_network, Toast.LENGTH_LONG).show();
            }else{
                mBooksJSONFetcher.parseResults(string);
                ArrayList<BookResult> results = mBooksJSONFetcher.getBookResults();
                if(results.size() == 0) Toast.makeText(activity, R.string.no_results, Toast.LENGTH_LONG).show();
                activity.mBookResultsAdapter.setBooksResults(mBooksJSONFetcher.getBookResults());
                activity.mBookResultsAdapter.notifyDataSetChanged();
            }
            activity.mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(Void... voids) {
            return mBooksJSONFetcher.getJSON(mQueryString);
        }
    }
}
