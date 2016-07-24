package com.tomokey.helloandroid.service;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tomokey.helloandroid.R;
import com.tomokey.helloandroid.app.MyApp;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * サービスクラスの基底クラス
 */
public abstract class AbstractService {
    // APIサーバーのURL
    private static final String BASE_URL = MyApp.getInstance().getString(R.string.api_host);

    /**
     * GSONインスタンスを生成します。
     *
     * @return {@link Gson}
     */
    private static Gson createGson() {
        return new GsonBuilder().create();
    }

    /**
     * サービスインスタンスを生成します。
     *
     * @param clazz 生成するサービスクラス
     * @param <T>   APIインターフェース
     * @return APIインターフェースを実装したサービスインスタンス
     */
    protected <T> T createService(Class<T> clazz) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor(message -> Log.i("HTTP", message)).setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        return new Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(createGson()))
                .build()
                .create(clazz);
    }
}
