package com.datavim.chatapp.utils;

import android.content.Context;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;



/**
 * Created by apple on 24/10/15.
 */
public class CustomPicasso {
    private static Picasso picasso;
    public static Picasso getPicassoClient(final Context context) {
        if(picasso == null) {
            OkHttpClient picassoClient = new OkHttpClient();
            picassoClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request newRequest = chain.request().newBuilder().build();
                    return chain.proceed(newRequest);
                }
            });
            picasso = new Picasso.Builder(context).downloader(new OkHttpDownloader(picassoClient)).build();
        }
        return picasso;
    }
}
