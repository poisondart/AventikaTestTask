package ru.testtask.aventika.aventicatesttask;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class DetailBookActivity extends AppCompatActivity {

    private ImageView mImageView;
    private TextView mTitleView, mAuthorView, mPublisherView, mPublishDateView,
    mDescView, mPageCountView, mCategory;
    private ProgressBar mProgressBar;
    private String mSelfQuery;

    public static final String TITLE = "aventika.title";
    public static final String AUTHORS = "aventika.authors";
    public static final String FULL_LINK = "aventika.fullLink";

    public static Intent newIntent(Context context, String title, String authors, String fullLink){
        Intent intent = new Intent(context, DetailBookActivity.class);
        intent.putExtra(TITLE, title);
        intent.putExtra(AUTHORS, authors);
        intent.putExtra(FULL_LINK, fullLink);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_book);
        initUI();
        String title = getIntent().getStringExtra(TITLE);
        String author = getIntent().getStringExtra(AUTHORS);
        initIntentViews(title, author);
        mSelfQuery = getIntent().getStringExtra(FULL_LINK);
        if(mSelfQuery != null && isOnline()){
            new FetchBookTask(mSelfQuery, DetailBookActivity.this).execute();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void initUI(){
        mImageView = findViewById(R.id.main_image);
        mProgressBar = findViewById(R.id.detail_progressbar);
        mTitleView = findViewById(R.id.fullTitleView);
        mAuthorView = findViewById(R.id.fullAuthorView);
        mPublisherView = findViewById(R.id.publisherTextView);
        mPublishDateView = findViewById(R.id.publisherDateTextView);
        mDescView = findViewById(R.id.descTextView);
        mPageCountView = findViewById(R.id.pageTextView);
        mCategory = findViewById(R.id.categoryView);
    }

    private void initIntentViews(String t, String a){
        if(t != null) mTitleView.setText(t);
        else mTitleView.setText("???");
        if(a != null) mAuthorView.setText(a);
        else mAuthorView.setText("???");
    }

    private static class FetchBookTask extends AsyncTask<Void, Void, String> {
        private String mQueryString;
        private WeakReference<DetailBookActivity> mReference;
        private FullBookJSONFetcher mFullBookJSONFetcher;
        public FetchBookTask(String query, DetailBookActivity reference) {
            mQueryString = query;
            mReference = new WeakReference<>(reference);
            mFullBookJSONFetcher = new FullBookJSONFetcher();
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DetailBookActivity activity = mReference.get();
            activity.mProgressBar.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            DetailBookActivity activity = mReference.get();
            if(string == null){
                Toast.makeText(activity, R.string.no_network, Toast.LENGTH_LONG).show();
            }else{
                mFullBookJSONFetcher.parseResults(string);

                FullBook fullBook = mFullBookJSONFetcher.getFullBook();

                activity.mPublisherView.setText(activity.getString(R.string.publisher, fullBook.getPublisher()));
                activity.mPublishDateView.setText(activity.getString(R.string.publisherDate, fullBook.getPublishDate()));
                activity.mDescView.setText(fullBook.getDesc());
                activity.mPageCountView.setText(activity.getString(R.string.pageCount, fullBook.getPageCount()));
                activity.mCategory.setText(activity.getString(R.string.category, fullBook.getCategory()));
                GlideApp.with(activity)
                        .load(fullBook.getImageLink())
                        .into(activity.mImageView);
            }

            activity.mProgressBar.setVisibility(ProgressBar.GONE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            return mFullBookJSONFetcher.getJSON(mQueryString);
        }
    }
}
