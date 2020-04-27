package com.tambola.game;

import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.okhttp.LogbookInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Utils {

  public static Retrofit getRetrofit(String baseUrl, Logbook logbook) {
    OkHttpClient client = (new Builder()).addInterceptor(new LogbookInterceptor(logbook)).build();
    return (new retrofit2.Retrofit.Builder()).client(client).baseUrl(baseUrl).addConverterFactory(
        GsonConverterFactory.create()).build();
  }

  public static Retrofit getRetrofit(String baseUrl) {
    OkHttpClient client = (new Builder()).build();
    return (new retrofit2.Retrofit.Builder()).client(client).baseUrl(baseUrl).addConverterFactory(
        GsonConverterFactory.create()).build();
  }

}
