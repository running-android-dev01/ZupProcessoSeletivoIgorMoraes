package com.moraes.igor.zupprocessoseletivo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

class ImageDownloader {
    private static final String TAG = ImageDownloader.class.getName();
    private static final int HARD_CACHE_CAPACITY = 10;
    private static final int DELAY_BEFORE_PURGE = 10 * 1000;
    private final static ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache =
            new ConcurrentHashMap<>(HARD_CACHE_CAPACITY / 2);
    private final Context ctx;
    private final File cacheDir;
    private final HashMap<String, Bitmap> sHardBitmapCache =
            new LinkedHashMap<String, Bitmap>(HARD_CACHE_CAPACITY / 2, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(LinkedHashMap.Entry<String, Bitmap> eldest) {
                    if (size() > HARD_CACHE_CAPACITY) {
                        sSoftBitmapCache.put(eldest.getKey(), new SoftReference<>(eldest.getValue()));
                        return true;
                    } else return false;
                }
            };
    private final Handler purgeHandler = new Handler();
    private final Runnable purger = this::clearCache;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    ImageDownloader(Context ctx) {
        this.ctx = ctx;
        cacheDir = ctx.getCacheDir();
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }


    private static boolean cancelPotentialDownload(String url, AppCompatImageView imageView) {
        BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

        if (bitmapDownloaderTask != null) {
            String bitmapUrl = bitmapDownloaderTask.url;
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                bitmapDownloaderTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    private static BitmapDownloaderTask getBitmapDownloaderTask(AppCompatImageView imageView) {
        if (imageView != null) {
            Object drawable = imageView.getTag();
            if (drawable instanceof DownloadedObject) {
                DownloadedObject downloadedDrawable = (DownloadedObject) drawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }

    void download(String url, AppCompatImageView imageView) {
        resetPurgeTimer();
        Bitmap bitmap = getBitmapFromCache(url);

        if (bitmap == null) {
            imageView.setImageDrawable(ContextCompat.getDrawable(imageView.getContext(), R.drawable.ic_block_black_24dp));
            forceDownload(url, imageView);
        } else {
            cancelPotentialDownload(url, imageView);
            imageView.setImageBitmap(bitmap);
        }
    }

    private void forceDownload(String url, AppCompatImageView imageView) {
        if (url == null) {
            imageView.setImageDrawable(null);
            return;
        }

        if (cancelPotentialDownload(url, imageView)) {
            if (ConnectivityManagerHelper.isConnectedOrConnecting(ctx)) {
                BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
                DownloadedObject downloadedDrawable = new DownloadedObject(task);
                imageView.setImageDrawable(ContextCompat.getDrawable(imageView.getContext(), R.drawable.ic_block_black_24dp));
                imageView.setTag(downloadedDrawable);
                imageView.setMinimumHeight(156);
                task.execute(url);
            }
        }
    }

    private Bitmap downloadBitmap(String urlString) {
        try {
            URL url = new URL(urlString);
            return downloadBitmap(url);
        } catch (MalformedURLException e) {
            Log.e(TAG, "ERRO = ", e);
            return null;
        }
    }

    private Bitmap downloadBitmap(URL url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return BitmapFactory.decodeStream(connection.getInputStream());
            } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                return BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_block_black_24dp);
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "ERRO = ", e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void addBitmapToCache(String url, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (sHardBitmapCache) {
                sHardBitmapCache.put(url, bitmap);
            }
        }
    }

    private Bitmap getBitmapFromCache(String url) {
        synchronized (sHardBitmapCache) {
            final Bitmap bitmap = sHardBitmapCache.get(url);
            if (bitmap != null) {
                sHardBitmapCache.remove(url);
                sHardBitmapCache.put(url, bitmap);
                return bitmap;
            }
        }
        SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(url);
        if (bitmapReference != null) {
            final Bitmap bitmap = bitmapReference.get();
            if (bitmap != null) {
                return bitmap;
            } else {
                sSoftBitmapCache.remove(url);
            }
        }

        String filename = String.valueOf(url.hashCode());
        File f = new File(cacheDir, filename);
        Bitmap b = getBitmap(f);
        if (b != null) {
            return b;
        }
        return null;
    }

    private Bitmap getBitmap(File f) {
        try {
            int tamanhoImagem = 100;

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            int larguraTmp = o.outWidth, height_tmp = o.outHeight;
            while (true) {
                if (larguraTmp / 2 < tamanhoImagem || height_tmp / 2 < tamanhoImagem)
                    break;
                larguraTmp /= 2;
                height_tmp /= 2;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "ERRO = ", e);
        }
        return null;
    }

    private void clearCache() {
        sHardBitmapCache.clear();
        sSoftBitmapCache.clear();
    }

    private void resetPurgeTimer() {
        purgeHandler.removeCallbacks(purger);
        purgeHandler.postDelayed(purger, DELAY_BEFORE_PURGE);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void saveBitMap(String url, Bitmap bitmap) {
        try {
            if (bitmap != null) {
                String filename = String.valueOf(url.hashCode());
                File f = new File(cacheDir, filename);
                f.createNewFile();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                byte[] bitmapdata = bos.toByteArray();

                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            }
        } catch (Exception ex) {
            Log.e(TAG, "ERRO = ", ex);
        }
    }

    class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<AppCompatImageView> imageViewReference;
        private String url;

        public BitmapDownloaderTask(AppCompatImageView imageView) {
            imageViewReference = new WeakReference<>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            url = params[0];
            return downloadBitmap(url);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }
            addBitmapToCache(url, bitmap);
            saveBitMap(url, bitmap);

            //if (imageViewReference != null) {
            AppCompatImageView imageView = imageViewReference.get();
            BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
            if (this == bitmapDownloaderTask && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            //}
        }
    }

    class DownloadedObject {
        private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

        public DownloadedObject(BitmapDownloaderTask bitmapDownloaderTask) {
            super();
            bitmapDownloaderTaskReference = new WeakReference<>(bitmapDownloaderTask);
        }

        public BitmapDownloaderTask getBitmapDownloaderTask() {
            return bitmapDownloaderTaskReference.get();
        }
    }
}
