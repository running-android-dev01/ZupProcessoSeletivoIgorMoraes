package com.moraes.igor.zupprocessoseletivo;

import android.content.Context;

public interface HomeListener {
    Context getContext();
    ImageDownloader getImageDownloader();
    void refreshList();
    void viewDetail(Movie movie);
}
