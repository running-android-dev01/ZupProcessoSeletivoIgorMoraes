package com.moraes.igor.zupprocessoseletivo;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoadMovieProgress extends AsyncTask<String, String, Movie> {
    private ProgressDialog progresso;
    private HomeListener homeListener;

    LoadMovieProgress(HomeListener homeListener){
        this.homeListener = homeListener;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progresso = ProgressDialog.show(homeListener.getContext(), "", "Searching for movie");
    }

    @Override
    protected void onPostExecute(Movie movie) {
        if (progresso != null) {
            progresso.dismiss();
        }
        homeListener.refreshList();
        super.onPostExecute(movie);
    }

    @Override
    protected Movie doInBackground(String... strings) {
        Movie movie = getMovie(strings[0]);
        if (movie!=null && Boolean.parseBoolean(movie.response)){
            DbHelper mDbHelper = new DbHelper(homeListener.getContext());
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            String selection = "imdbID = ?";
            String[] selectionArgs = { movie.imdbID };
            db.delete("Movie", selection, selectionArgs);
            db.delete("Rating", selection, selectionArgs);

            ContentValues valuesMovie = new ContentValues();
            valuesMovie.put("imdbID", movie.imdbID);
            valuesMovie.put("title", movie.title);
            valuesMovie.put("year", movie.year);
            valuesMovie.put("rated", movie.rated);
            valuesMovie.put("released", movie.released);
            valuesMovie.put("runtime", movie.runtime);
            valuesMovie.put("genre", movie.genre);
            valuesMovie.put("director", movie.director);
            valuesMovie.put("writer", movie.writer);
            valuesMovie.put("actors", movie.actors);
            valuesMovie.put("plot", movie.plot);
            valuesMovie.put("language", movie.language);
            valuesMovie.put("country", movie.country);
            valuesMovie.put("awards", movie.awards);
            valuesMovie.put("poster", movie.poster);
            valuesMovie.put("metascore", movie.metascore);
            valuesMovie.put("imdbRating", movie.imdbRating);
            valuesMovie.put("imdbVotes", movie.imdbVotes);
            valuesMovie.put("type", movie.type);
            valuesMovie.put("dvd", movie.dvd);
            valuesMovie.put("boxOffice", movie.boxOffice);
            valuesMovie.put("production", movie.production);
            valuesMovie.put("website", movie.website);
            valuesMovie.put("response", movie.response);

            db.insert("Movie", null, valuesMovie);

            for (Rating rating: movie.ratings) {
                ContentValues valuesRating = new ContentValues();
                valuesRating.put("ratingKey", rating.ratingKey);
                valuesRating.put("imdbID", rating.imdbID);
                valuesRating.put("source", rating.source);
                valuesRating.put("value", rating.value);

                db.insert("Rating", null, valuesRating);
            }

        }
        return movie;
    }

    private String getJson(String link){
        String retorno;
        URL url;
        StringBuilder response = new StringBuilder();
        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url");
        }

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(false);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");
            //System.setProperty("http.agent", "Chrome");

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");



            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }

            retorno = response.toString();
        }
        return retorno;
    }

    private Movie getMovie(String title) {
        Movie movie = new Movie();
        try {
            String json = getJson("http://www.omdbapi.com/?t=" + title + "&apikey=15847595");
            movie.parseJson(json);
        } catch (Exception e) {
            movie = null;
            Log.e(this.getClass().getName(), "ERRO = ", e);
        }
        return movie;
    }


}
