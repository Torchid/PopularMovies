package com.direyorkie.popularmovies.com.direyorkie.popularmovies.utilities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rachel on 10/4/2015.
 */
public class Movie implements Parcelable {
    public String poster;
    public String title;
    public String data;
    public String overview;
    public double rating;

    public Movie() {
        this.poster = "";
        this.title = "";
        this.data = "";
        this.overview = "";
        this.rating = -1;
    }

    public Movie(Parcel in) {
        this.poster = in.readString();
        this.title = in.readString();
        this.data = in.readString();
        this.overview = in.readString();
        this.rating = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.poster);
        dest.writeString(this.title);
        dest.writeString(this.data);
        dest.writeString(this.overview);
        dest.writeDouble(this.rating);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Creator<Movie>() {

        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
