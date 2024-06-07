package com.example.gymapp;

import android.os.Handler;
import android.os.Looper;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkUtils {

    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public interface Callback {
        void onComplete(String result);
    }

    public static void postData(String url, String[] field, String[] data, Callback callback) {
        executor.execute(() -> {
            PutData putData = new PutData(url, "POST", field, data);
            String result = "";
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    result = putData.getResult();
                }
            }
            String finalResult = result;
            new Handler(Looper.getMainLooper()).post(() -> callback.onComplete(finalResult));
        });
    }
}
