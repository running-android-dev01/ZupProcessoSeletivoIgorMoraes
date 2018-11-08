package com.moraes.igor.zupprocessoseletivo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements HomeListener {

    private RecyclerView recycler_view;
    private HomeAdapter adapter;
    public ImageDownloader download;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener((v) -> addMoviewDialog(""));

        download = new ImageDownloader(this);

        recycler_view = findViewById(R.id.recycler_view);

        setupRecycler();
        refreshList();
    }

    private void setupRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_view.setLayoutManager(layoutManager);

        adapter = new HomeAdapter(HomeActivity.this);
        recycler_view.setAdapter(adapter);

        recycler_view.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

    private void addMoviewDialog(final String tittle){
        final EditText editText = new EditText(getContext());
        editText.setText(tittle);

        new AlertDialog.Builder(getContext())
                .setView(editText)
                .setTitle(getString(R.string.dialog_name_movie))
                .setPositiveButton(R.string.ok, (d1, w1) -> {
                    if (TextUtils.isEmpty(editText.getText().toString())){

                        new AlertDialog.Builder(getContext())
                                .setTitle(getString(R.string.dialog_name_movie_required))
                                .setPositiveButton(R.string.ok, (d2, w2) -> addMoviewDialog(tittle))
                                .create()
                                .show();

                        return;
                    }
                    new LoadMovieProgress(HomeActivity.this).execute(editText.getText().toString());
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public ImageDownloader getImageDownloader(){
        return this.download;
    }

    @Override
    public void refreshList() {
        DbHelper mDbHelper = new DbHelper(getContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String[] projectionMovie = {
                "imdbID",
                "title",
                "year",
                "rated",
                "released",
                "runtime",
                "genre",
                "director",
                "writer",
                "actors",
                "plot",
                "language",
                "country",
                "awards",
                "poster",
                "metascore",
                "imdbRating",
                "imdbVotes",
                "type",
                "dvd",
                "boxOffice",
                "production",
                "website",
                "response"
        };

        String sortOrder = "title";
        Cursor cursorMovie = db.query("Movie", projectionMovie, null, null, null, null, sortOrder);

        List listMovie = new ArrayList();
        while(cursorMovie.moveToNext()) {
            Movie movie = new Movie();
            movie.title = cursorMovie.getString(cursorMovie.getColumnIndexOrThrow("title"));
            movie.year = cursorMovie.getString(cursorMovie.getColumnIndexOrThrow("year"));
            movie.rated = cursorMovie.getString(cursorMovie.getColumnIndexOrThrow("rated"));
            movie.released = cursorMovie.getString(cursorMovie.getColumnIndexOrThrow("released"));
            movie.runtime = cursorMovie.getString(cursorMovie.getColumnIndexOrThrow("runtime"));
            movie.genre = cursorMovie.getString(cursorMovie.getColumnIndexOrThrow("genre"));
            movie.director = cursorMovie.getString(cursorMovie.getColumnIndexOrThrow("director"));
            movie.writer = cursorMovie.getString(cursorMovie.getColumnIndexOrThrow("writer"));
            movie.actors = cursorMovie.getString(cursorMovie.getColumnIndexOrThrow("actors"));
            movie.plot = cursorMovie.getString(cursorMovie.getColumnIndexOrThrow("plot"));
            movie.language = cursorMovie.getString(cursorMovie.getColumnIndexOrThrow("language"));
            movie.country = cursorMovie.getString(cursorMovie.getColumnIndexOrThrow("country"));
            movie.awards = cursorMovie.getString(cursorMovie.getColumnIndexOrThrow("awards"));
            movie.poster = cursorMovie.getString(cursorMovie.getColumnIndexOrThrow("poster"));
            movie.metascore = cursorMovie.getString(cursorMovie.getColumnIndexOrThrow("metascore"));
            movie.imdbRating = cursorMovie.getString(cursorMovie.getColumnIndexOrThrow("imdbRating"));
            movie.imdbVotes = cursorMovie.getString(cursorMovie.getColumnIndexOrThrow("imdbVotes"));
            movie.imdbID = cursorMovie.getString(cursorMovie.getColumnIndexOrThrow("imdbID"));
            movie.type = cursorMovie.getString(cursorMovie.getColumnIndexOrThrow("type"));
            movie.dvd = cursorMovie.getString(cursorMovie.getColumnIndexOrThrow("dvd"));
            movie.boxOffice = cursorMovie.getString(cursorMovie.getColumnIndexOrThrow("boxOffice"));
            movie.production = cursorMovie.getString(cursorMovie.getColumnIndexOrThrow("production"));
            movie.website = cursorMovie.getString(cursorMovie.getColumnIndexOrThrow("website"));
            movie.response = cursorMovie.getString(cursorMovie.getColumnIndexOrThrow("response"));

            String[] projectionRating = {
                    "ratingKey",
                    "imdbID",
                    "source",
                    "value"
            };

            String selectionRating = "imdbID = ?";
            String[] selectionArgsRating = { movie.imdbID };

            sortOrder = "source";
            Cursor cursorRating = db.query("Rating", projectionRating, selectionRating, selectionArgsRating, null, null, sortOrder);

            movie.ratings = new ArrayList<>();
            while(cursorRating.moveToNext()) {
                Rating rating = new Rating();
                rating.ratingKey = cursorRating.getString(cursorRating.getColumnIndexOrThrow("ratingKey"));
                rating.imdbID = cursorRating.getString(cursorRating.getColumnIndexOrThrow("imdbID"));
                rating.source = cursorRating.getString(cursorRating.getColumnIndexOrThrow("source"));
                rating.value = cursorRating.getString(cursorRating.getColumnIndexOrThrow("value"));

                movie.ratings.add(rating);
            }
            cursorRating.close();
            listMovie.add(movie);

        }
        cursorMovie.close();

        adapter.refreshList(listMovie);
    }

    @Override
    public void viewDetail(Movie movie) {
        Intent i = new Intent(HomeActivity.this, DetailActivity.class);
        i.putExtra(DetailActivity.PARAM_MOVIE, movie);
        startActivity(i);
    }
}
