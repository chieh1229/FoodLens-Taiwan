package com.example.myapplication.models;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FastApiService {
    @Multipart
    @POST("/food")
    Call<RecogResult> FoodImageUpload(@Part MultipartBody.Part image);


    @Multipart
    @POST("/ocr")
    Call<OcrResult> OcrTextUpload(@Part MultipartBody.Part image);
}
