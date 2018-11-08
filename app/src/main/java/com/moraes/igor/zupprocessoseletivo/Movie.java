package com.moraes.igor.zupprocessoseletivo;

import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;
import android.os.Parcel;

class Movie implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    Movie(){

    }

    private Movie(Parcel in){
        this.title = in.readString();
        this.year = in.readString();
        this.rated = in.readString();
        this.released = in.readString();
        this.runtime = in.readString();
        this.genre = in.readString();
        this.director = in.readString();
        this.writer = in.readString();
        this.actors = in.readString();
        this.plot = in.readString();
        this.language = in.readString();
        this.country = in.readString();
        this.awards = in.readString();
        this.poster = in.readString();
        this.ratings = in.readArrayList(Rating.class.getClassLoader());
        this.metascore = in.readString();
        this.imdbRating = in.readString();
        this.imdbVotes = in.readString();
        this.imdbID = in.readString();
        this.type = in.readString();
        this.dvd = in.readString();
        this.boxOffice = in.readString();
        this.production = in.readString();
        this.website = in.readString();
        this.response = in.readString();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.year);
        dest.writeString(this.rated );
        dest.writeString(this.released);
        dest.writeString(this.runtime);
        dest.writeString(this.genre);
        dest.writeString(this.director);
        dest.writeString(this.writer);
        dest.writeString(this.actors);
        dest.writeString(this.plot);
        dest.writeString(this.language);
        dest.writeString(this.country);
        dest.writeString(this.awards);
        dest.writeString(this.poster);
        dest.writeList(this.ratings);
        dest.writeString(this.metascore);
        dest.writeString(this.imdbRating);
        dest.writeString(this.imdbVotes);
        dest.writeString(this.imdbID);
        dest.writeString(this.type);
        dest.writeString(this.dvd);
        dest.writeString(this.boxOffice);
        dest.writeString(this.production);
        dest.writeString(this.website);
        dest.writeString(this.response);
    }

    @Override
    public int describeContents() {
        return 0;
    }



    String title;
    String year;
    String rated;
    String released;
    String runtime;
    String genre;
    String director;
    String writer;
    String actors;
    String plot;
    String language;
    String country;
    String awards;
    String poster;
    ArrayList<Rating> ratings;
    String metascore;
    String imdbRating;
    String imdbVotes;
    String imdbID;
    String type;
    String dvd;
    String boxOffice;
    String production;
    String website;
    String response;


    void parseJson(String s) throws JSONException {
        JSONObject json = new JSONObject(s);

        this.title = json.getString("Title");
        this.year = json.getString("Year");
        this.rated = json.getString("Rated");
        this.released = json.getString("Released");
        this.runtime = json.getString("Runtime");
        this.genre = json.getString("Genre");
        this.director = json.getString("Director");
        this.writer = json.getString("Writer");
        this.actors = json.getString("Actors");
        this.plot = json.getString("Plot");
        this.language = json.getString("Language");
        this.country = json.getString("Country");
        this.awards = json.getString("Awards");
        this.poster = json.getString("Poster");
        //public List<Rating> Ratings;
        this.metascore = json.getString("Metascore");
        this.imdbRating = json.getString("imdbRating");
        this.imdbVotes = json.getString("imdbVotes");
        this.imdbID = json.getString("imdbID");
        this.type = json.getString("Type");
        this.dvd = json.getString("DVD");
        this.boxOffice = json.getString("BoxOffice");
        this.production = json.getString("Production");
        this.website = json.getString("Website");
        this.response = json.getString("Response");

        this.ratings = new ArrayList<>();
        JSONArray sRatings = json.getJSONArray("Ratings");
        for (int i=0; i < sRatings.length(); i++) {
            JSONObject jsonObject = sRatings.getJSONObject(i);
            Rating rating = new Rating();
            rating.ratingKey = UUID.randomUUID().toString();
            rating.imdbID = imdbID;
            rating.source = jsonObject.getString("Source");
            rating.value = jsonObject.getString("Value");

            ratings.add(rating);
        }
    }
}
