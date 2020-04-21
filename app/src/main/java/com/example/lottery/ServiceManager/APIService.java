package com.example.lottery.ServiceManager;

import com.example.lottery.ImageRequest;
import com.example.lottery.ImageResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers("Content-Type: application/json")
    @POST("expression/")
    Call<ImageResponse> imagePass(

            @Body ImageRequest body);

//

}
