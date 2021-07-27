package com.example.myapplication.notifiche;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAFhv7UWo:APA91bHVDS8oK-0HWmzRvIiXTp31Yuj2n7FSLabmcINE8DPf9T9k02HyUGLUJfghcvTTA8Fj4ABgJZDA9KgbFvrGZHtFHhFRBj0xYVNZkHVSkjbWg9aGmK-Xk04TC-O2du53toDt8zJ6" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")
    Call<Risposta> sendNotifcation(@Body NotificationSender body);
}