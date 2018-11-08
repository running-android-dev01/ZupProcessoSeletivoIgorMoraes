package com.moraes.igor.zupprocessoseletivo;

import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

class HomeViewHolder extends RecyclerView.ViewHolder {
    final AppCompatImageView imgPoster;
    final AppCompatTextView txtTitle;
    final AppCompatTextView txtPlot;

    HomeViewHolder(View itemView) {
        super(itemView);

        imgPoster =  itemView.findViewById(R.id.imgPoster);
        txtTitle =  itemView.findViewById(R.id.txtTitle);
        txtPlot =  itemView.findViewById(R.id.txtPlot);
    }
}
