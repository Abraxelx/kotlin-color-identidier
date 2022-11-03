package com.abraxel.color_identifier;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

interface ColorApi {
    @Headers("Content-Type: text/html")
    @GET("id")
    Call<String> getColorName(@Query("hex") String hexCode);
}
