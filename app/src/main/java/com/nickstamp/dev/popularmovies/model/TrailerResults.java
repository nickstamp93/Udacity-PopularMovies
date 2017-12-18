package com.nickstamp.dev.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Dev on 26/1/2017.
 */

public class TrailerResults {

    @SerializedName("results")
    @Expose
    private ArrayList<Trailer> trailers = new ArrayList<>();

    /**
     * @return The trailers
     */
    public ArrayList<Trailer> getTrailers() {
        return trailers;
    }

    /**
     * @param trailers The trailers
     */
    public void setTrailers(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
    }


    public class Trailer implements Parcelable {

        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("key")
        @Expose
        private String key;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("type")
        @Expose
        private String type;

        protected Trailer(Parcel in) {
            id = in.readString();
            key = in.readString();
            name = in.readString();
            type = in.readString();
        }

        public final Creator<Trailer> CREATOR = new Creator<Trailer>() {

            @Override
            public Trailer createFromParcel(Parcel in) {
                return new Trailer(in);
            }

            @Override
            public Trailer[] newArray(int size) {
                return new Trailer[size];
            }

        };


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(id);
            parcel.writeString(key);
            parcel.writeString(name);
            parcel.writeString(type);

        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }

}
