package com.moraes.igor.zupprocessoseletivo;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;

import java.net.MalformedURLException;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = DetailActivity.class.getName();
    public static final String PARAM_MOVIE = TAG + ".MOVIE";
    public ImageDownloader download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Movie movie = getIntent().getParcelableExtra(PARAM_MOVIE);
        download = new ImageDownloader(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(movie.title);
        setSupportActionBar(toolbar);

        AppCompatImageView collapsingToolbarImageView = findViewById(R.id.placeholder);
        collapsingToolbarImageView.setImageResource(R.drawable.ic_block_black_24dp);

        try {
            URL url = new URL(movie.poster);
            download.download(url.toString(), collapsingToolbarImageView);
        } catch (MalformedURLException e) {
            Log.e(TAG, "ERRO = ", e);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        AppCompatTextView txtTitle = findViewById(R.id.txtTitle);
        AppCompatTextView txtGenre = findViewById(R.id.txtGenre);
        AppCompatTextView txtRated = findViewById(R.id.txtRated);
        AppCompatTextView txReleased = findViewById(R.id.txReleased);
        AppCompatTextView txtPlot = findViewById(R.id.txtPlot);
        AppCompatTextView txRuntime = findViewById(R.id.txRuntime);
        AppCompatTextView txDirector = findViewById(R.id.txDirector);
        AppCompatTextView txWriter = findViewById(R.id.txWriter);
        AppCompatTextView txActors = findViewById(R.id.txActors);
        AppCompatTextView txCountry = findViewById(R.id.txCountry);
        AppCompatTextView txLanguage = findViewById(R.id.txLanguage);
        AppCompatTextView txAwards = findViewById(R.id.txAwards);
        AppCompatTextView txProduction = findViewById(R.id.txProduction);
        AppCompatTextView txWebsite = findViewById(R.id.txWebsite);

        txtTitle.setText(String.format("%s (%s)", movie.title, movie.year));
        txtGenre.setText(movie.genre);
        txtRated.setText(movie.rated);
        txReleased.setText(movie.released);
        txtPlot.setText(movie.plot);
        txRuntime.setText(movie.runtime);
        txDirector.setText(movie.director);
        txWriter.setText(movie.writer);
        txActors.setText(movie.actors);
        txCountry.setText(movie.country);
        txLanguage.setText(movie.language);
        txAwards.setText(movie.awards);
        txProduction.setText(movie.production);
        txWebsite.setText(movie.website);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
