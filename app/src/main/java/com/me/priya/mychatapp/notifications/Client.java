package com.me.priya.mychatapp.notifications;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Priya Jain on 12,December,2018
 */
public class Client {

  private static Retrofit retrofit = null;

  public static Retrofit getClient(String url) {
    if (retrofit == null) {
      retrofit = new Retrofit.Builder()
          .baseUrl(url)
          .addConverterFactory(GsonConverterFactory.create())
          .build();
    }
    return retrofit;
  }
}
