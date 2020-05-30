package com.paul9834.exoplayerchannel.Controllers;

import com.paul9834.exoplayerchannel.Entities.Canal;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface Canales {


    @GET("canalalt")
    Call<List<Canal>> getPosts();

    @GET("canalalt/{id}")
    Call<List<Canal>> getPosts(@Path("id") String id);


}


