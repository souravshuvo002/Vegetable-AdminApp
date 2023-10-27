package com.sourav.adminapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sourav.adminapp.Model.Review;
import com.sourav.adminapp.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private List<Review> ratingList;
    private Context mCtx;

    public ReviewsAdapter(List<Review> ratings, Context mCtx) {
        this.ratingList = ratings;
        this.mCtx = mCtx;

        setHasStableIds(true);
    }

    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_reviews_item, parent, false);
        return new ReviewsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ReviewsAdapter.ViewHolder holder, final int position) {
        final Review rating = ratingList.get(position);


        holder.textViewMainRating.setText(getRatingText(rating.getRating()));

        holder.textViewComment.setText(rating.getText());
        holder.textViewDate.setText(rating.getDate_added());
        holder.textViewUsername.setText(rating.getAuthor());
        holder.ratingBar.setRating(rating.getRating());
    }

    @Override
    public int getItemCount() {
        return ratingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewMainRating, textViewDate, textViewUsername, textViewComment;
        public RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewMainRating = (TextView) itemView.findViewById(R.id.textViewMainRating);
            textViewDate = (TextView) itemView.findViewById(R.id.textViewDate);
            textViewUsername = (TextView) itemView.findViewById(R.id.textViewUsername);
            textViewComment = (TextView) itemView.findViewById(R.id.textViewComment);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    private String getRatingText(int rating)
    {
        String ratingText;

        switch (rating) {
            case 5:
                ratingText = "Excellent";
                break;
            case 4:
                ratingText = "Very Good";
                break;
            case 3:
                ratingText = "Quite Ok";
                break;
            case 2:
                ratingText = "Not Good";
                break;
            case 1:
                ratingText = "Very Bad";
                break;
            default:
                ratingText = "Very Good";
        }
        return ratingText;
    }
}