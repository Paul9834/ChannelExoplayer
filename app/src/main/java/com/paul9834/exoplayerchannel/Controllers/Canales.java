package com.paul9834.exoplayerchannel.Controllers;

import com.paul9834.exoplayerchannel.Entities.Canal;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Canales {


    @GET("canalalt")
    Call<List<Canal>> getPosts();

    @GET("canalalt/{id}")
    Call<List<Canal>> getPosts(@Path("id") String id);


    @GET("finishPlayback/{id}")
    Call<ResponseBody> stopPlayback(@Path("id") String id);

    @GET("startplayback/{id}")
    Call<ResponseBody> playPlayback(@Path("id") String id);


    @FormUrlEncoded
    @POST ("/api/savelog")
    Call<ResponseBody> saveLog(@Field("tvbox_id") String id, @Field("log_message") String log, @Field("warn_level") String warn_level);




}


