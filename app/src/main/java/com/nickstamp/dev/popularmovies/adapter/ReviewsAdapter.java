package com.nickstamp.dev.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nickstamp.dev.popularmovies.R;
import com.nickstamp.dev.popularmovies.model.ReviewResults;

import java.util.ArrayList;

/**
 * Created by Dev on 8/2/2017.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    public static final String TAG = ReviewsAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<ReviewResults.Review> mReviews;

    public ReviewsAdapter(Context context, ArrayList<ReviewResults.Review> reviews) {
        mContext = context;

        mReviews = reviews;
    }

    @Override
    public ReviewsAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_review, parent, false);
        return new ReviewsAdapter.ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapter.ReviewViewHolder holder, int position) {

        // If the number of reviews is odd, make first review full screen wide and the rest half-screen wide
        // for better usage of space
        if (mReviews.size() % 2 == 1 && position == 0) {
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
        }

        holder.bind(position);

    }

    @Override
    public int getItemCount() {
        if (mReviews == null) return 0;
        return mReviews.size();
    }

    public void setData(ArrayList<ReviewResults.Review> reviews) {
        this.mReviews = reviews;
        notifyDataSetChanged();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView content, author;

        public ReviewViewHolder(View itemView) {
            super(itemView);

            content = (TextView) itemView.findViewById(R.id.tvReviewContent);
            author = (TextView) itemView.findViewById(R.id.tvReviewAuthor);

        }

        public void bind(int pos) {

            ReviewResults.Review review = mReviews.get(pos);

            content.setText("\"" + review.getContent() + "\"");
            author.setText(review.getAuthor());


        }
    }
}
