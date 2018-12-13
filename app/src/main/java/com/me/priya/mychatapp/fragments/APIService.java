package com.me.priya.mychatapp.fragments;

import com.me.priya.mychatapp.notifications.MyResponse;
import com.me.priya.mychatapp.notifications.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Priya Jain on 12,December,2018
 */
public interface APIService {
@Headers({
    "Content-Type:application/json",
    "Authorization:key=AAAADulSywU:APA91bHu0YXnW235yFzlMcPwPk2A83H1pbwzfgLVkcbQF2qkfDF0MDgzcnypN-macoBy3a9JxhdlHOAohOq32Sb8lDTkYBaXJxh5NjbjBnW2XkQ-RErq1op-E4c_SdY_v-y0TpRC4QuO"
    })

  @POST("fcm/send")
  Call<MyResponse> sendNotification(@Body Sender body);

}
