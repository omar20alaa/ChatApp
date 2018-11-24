package com.chat.chatapp.Activity.Api;

import com.chat.chatapp.Activity.Notification.MyResponse;
import com.chat.chatapp.Activity.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key = AAAA4YAdNDE:APA91bG-ey-1__Xf07Lmtib5bkaq9tjETlvO6CjyvfBzposov6fCB-udLhhWKAt7lqJH7bi0teNSJs0DIkn2TbEI8o7idWvdLjtzNIJms501SHO1vrNAq9nUNhkKzzRqMk1X2Bk2Ez7V"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
