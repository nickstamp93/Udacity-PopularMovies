package com.nickstamp.dev.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nickstamp.dev.popularmovies.R;
import com.nickstamp.dev.popularmovies.model.TrailerResults.Trailer;
import com.nickstamp.dev.popularmovies.utils.Utils;

import java.util.ArrayList;


/**
 * Created by Dev on 6/2/2017.
 */


public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.TrailerViewHolder> {


    public static final String TAG = VideosAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<Trailer> mTrailers;

    public VideosAdapter(Context context, ArrayList<Trailer> trailers) {
        mContext = context;

        mTrailers = trailers;
    }

    @Override
    public VideosAdapter.TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_trailer, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideosAdapter.TrailerViewHolder holder, int position) {

        holder.bind(position);

    }

    @Override
    public int getItemCount() {
        if (mTrailers == null) return 0;
        return mTrailers.size();
    }

    public void setData(ArrayList<Trailer> trailers) {
        this.mTrailers = trailers;
        notifyDataSetChanged();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tvTrailerTitle);

            itemView.setOnClickListener(this);
        }

        public void bind(int pos) {
            Trailer trailer = mTrailers.get(pos);

            title.setText(trailer.getName());


        }

        @Override
        public void onClick(View view) {

            String videoUri = Utils.buildYoutubeUri(mTrailers.get(getAdapterPosition()).getKey());

            Uri uri = Uri.parse(videoUri);

            mContext.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }

}
