package ru.testtask.aventika.aventicatesttask;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class BookResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<BookResult> mBookResults;

    public BookResultsAdapter(ArrayList<BookResult> bookResults) {
        super();
        mBookResults = bookResults;
    }

    public void setBooksResults(ArrayList<BookResult> bookResults){
        mBookResults = bookResults;
    }

    @NonNull
    @Override
    public BookResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item_card, parent, false);
        return new BookResultViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BookResultViewHolder bookResultViewHolder = (BookResultViewHolder) holder;
        bookResultViewHolder.bindBook(mBookResults.get(position));
    }

    @Override
    public int getItemCount() {
        return mBookResults.size();
    }

    private class BookResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private BookResult mBookResult;
        private TextView mTitleView, mAuthorsView;
        private ImageView mImageView;

        public BookResultViewHolder(View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.bookTitleTextView);
            mAuthorsView = itemView.findViewById(R.id.bookAuthorsTextView);
            mImageView = itemView.findViewById(R.id.thumnailImageView);
            itemView.setOnClickListener(this);
            mImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = DetailBookActivity.newIntent(view.getContext(),
                    mBookResult.getTitle(), mBookResult.getAuthors(), mBookResult.getSelfLink());
            view.getContext().startActivity(intent);
        }

        private void bindBook(BookResult bookResult){
            mBookResult = bookResult;
            mTitleView.setText(mBookResult.getTitle());
            mAuthorsView.setText(mBookResult.getAuthors());
            GlideApp.with(mTitleView.getContext())
                    .load(mBookResult.getSmallThumbnailLink())
                    .into(mImageView);
        }
    }
}
