package ru.testtask.aventika.aventicatesttask;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailBookActivity extends AppCompatActivity {

    private ImageView mImageView;
    private TextView mTitleView, mAuthorView, mPublisherView, mPublishDateView,
    mDescView, mPageCountView, mCategory;
    private ProgressBar mProgressBar;
    private String mSelfQuery;
    private String mFullThumbnailLink;

    public static final String TITLE = "aventika.title";
    public static final String AUTHORS = "aventika.authors";
    public static final String FULL_LINK = "aventika.fullLink";
    public static final String FULL_THUMBNAIL_LINK = "aventika.fullLink";

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

        mImageView = findViewById(R.id.main_image);
        mProgressBar = findViewById(R.id.detail_progressbar);
        mTitleView = findViewById(R.id.fullTitleView);
        mAuthorView = findViewById(R.id.fullAuthorView);
        mPublisherView = findViewById(R.id.publisherTextView);
        mPublishDateView = findViewById(R.id.publisherDateTextView);
        mDescView = findViewById(R.id.descTextView);
        mPageCountView = findViewById(R.id.pageTextView);
        mCategory = findViewById(R.id.categoryView);

        mTitleView.setText(getIntent().getStringExtra(TITLE));
        mAuthorView.setText(getIntent().getStringExtra(AUTHORS));
        mSelfQuery = getIntent().getStringExtra(FULL_LINK);
        mFullThumbnailLink = getIntent().getStringExtra(FULL_THUMBNAIL_LINK);

        GlideApp.with(getApplicationContext())
                .load(mFullThumbnailLink)
                .into(mImageView);

        new FetchBookTask(mSelfQuery, DetailBookActivity.this).execute();
    }

    private static class FetchBookTask extends AsyncTask<Void, Void, String> {
        private String mQueryString;
        private WeakReference<DetailBookActivity> mReference;
        public FetchBookTask(String query, DetailBookActivity reference) {
            mQueryString = query;
            mReference = new WeakReference<>(reference);
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
            try{
                JSONObject jsonObject = new JSONObject(string);
                int i = 0;
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

                    activity.mPublisherView.setText(activity.getString(R.string.publisher, publisher));
                    activity.mPublishDateView.setText(activity.getString(R.string.publisherDate, publishDate));
                    activity.mDescView.setText(desc);
                    activity.mPageCountView.setText(activity.getString(R.string.pageCount, pages));
                    activity.mCategory.setText(activity.getString(R.string.category, category));

                    GlideApp.with(activity)
                            .load(largeThumbnail)
                            .into(activity.mImageView);

            }catch (JSONException e){
                e.printStackTrace();
            }

            activity.mProgressBar.setVisibility(ProgressBar.GONE);
        }

        @Override
        protected String doInBackground(Void... voids) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String bookJSONString = null;

            try{

                Uri builtURI = Uri.parse(mQueryString).buildUpon()
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
