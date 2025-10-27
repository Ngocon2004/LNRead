package me.etylix.lnread;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // Singleton instance
    private static volatile RetrofitClient instance = null;
    private Retrofit retrofit;
    
    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .callTimeout(2, TimeUnit.MINUTES)
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .build();
    
    // Private constructor để ngăn khởi tạo trực tiếp
    private RetrofitClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("https://konovn.net/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }
    
    // Thread-safe getInstance method (Double-Checked Locking pattern)
    public static RetrofitClient getInstance() {
        if (instance == null) {
            synchronized (RetrofitClient.class) {
                if (instance == null) {
                    instance = new RetrofitClient();
                }
            }
        }
        return instance;
    }
    
    public Retrofit getRetrofit() {
        return retrofit;
    }
}
