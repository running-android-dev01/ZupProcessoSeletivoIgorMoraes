package com.moraes.igor.zupprocessoseletivo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class HomeAdapter extends RecyclerView.Adapter<HomeViewHolder> {
    private static final String TAG = HomeAdapter.class.getName();
    private List<Movie> lMovie;
    private HomeListener homeListener;

    HomeAdapter(HomeListener homeListener){
        this.homeListener = homeListener;
    }

    void refreshList(List<Movie> lMovie) {
        this.lMovie = lMovie;
        notifyDataSetChanged();
    }

    @Override
    @NonNull
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v = inflater.inflate(R.layout.adapter_home, parent, false);

        return new HomeViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder viewHolder, int position) {
        Movie movie = lMovie.get(position);

        try {
            URL url = new URL(movie.poster);
            homeListener.getImageDownloader().download(url.toString(), viewHolder.imgPoster);
        } catch (MalformedURLException e) {
            Log.e(TAG, "ERRO = ", e);
        }


        viewHolder.txtTitle.setText(String.format("%s (%s)", movie.title, movie.year));
        viewHolder.txtPlot.setText(movie.plot);

        viewHolder.itemView.setOnClickListener((v) -> homeListener.viewDetail(movie));
    }

    @Override
    public int getItemCount() {
        return lMovie != null ? lMovie.size() : 0;
    }
}
