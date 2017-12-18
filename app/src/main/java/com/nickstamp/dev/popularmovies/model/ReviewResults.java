package com.nickstamp.dev.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Dev on 26/1/2017.
 */

public class ReviewResults {

    @SerializedName("results")
    @Expose
    private ArrayList<Review> reviews = new ArrayList<>();

    /**
     * @return The reviews
     */
    public ArrayList<Review> getReviews() {
        return reviews;
    }

    /**
     * @param reviews The reviews
     */
    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }


    public class Review implements Parcelable{

        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("author")
        @Expose
        private String author;

        @SerializedName("content")
        @Expose
        private String content;

        protected Review(Parcel in) {
            id = in.readString();
            author = in.readString();
            content = in.readString();
        }

        public final Creator<Review> CREATOR = new Creator<Review>() {

            @Override
            public Review createFromParcel(Parcel in) {
                return new Review(in);
            }

            @Override
            public Review[] newArray(int size) {
                return new Review[size];
            }

        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(id);
            parcel.writeString(author);
            parcel.writeString(content);

        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }
    }

}
