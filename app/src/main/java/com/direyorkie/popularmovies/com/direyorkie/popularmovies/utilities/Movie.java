package com.direyorkie.popularmovies.com.direyorkie.popularmovies.utilities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rachel on 10/4/2015.
 */
public class Movie implements Parcelable {
    public String poster;
    public String title;
    public String year;
    public String length;
    public String rating;
    public String overview;

    public Movie() {
        this.poster = "";
        this.title = "";
        this.year = "";
        this.length = "";
        this.rating = "";
        this.overview = "";
    }

    public Movie(Parcel in) {
        this.poster = in.readString();
        this.title = in.readString();
        this.year = in.readString();
        this.length = in.readString();
        this.rating = in.readString();
        this.overview = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.poster);
        dest.writeString(this.title);
        dest.writeString(this.year);
        dest.writeString(this.length);
        dest.writeString(this.rating);
        dest.writeString(this.overview);
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
