package com.paul9834.exoplayerchannel.Entities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.paul9834.exoplayerchannel.RetrofitClient.RetrofitClient;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerStatus {


    private SharedPreferences sharedPreferences;
    private Context context;


    public PlayerStatus() {

    }

    public PlayerStatus(Context context) {
        this.context = context;
    }


    public void callServiceURLChannel() {
        Log.e("Registro", "Llama al servicio");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        String id = prefs.getString("id", "no id");

        Call<List<Canal>> call = RetrofitClient.getInstance().getCanal().getPosts("22");
        call.enqueue(new Callback<List<Canal>>() {
            @Override
            public void onResponse(Call<List<Canal>> call, retrofit2.Response<List<Canal>> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                List<Canal> posts = response.body();
                String url = "";
                for (Canal post : posts) {
                    String content = "";
                    content += "link : " + post.getalt_uri() + "\n";
                    url = post.getalt_uri();
                }
                //  Log.e("url del ID ", url);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("url", url);
                editor.apply();

            }
            @Override
            public void onFailure(Call<List<Canal>> call, Throwable t) {
            }
        });
    }


    public void pausePlayback() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        String id = prefs.getString("id", "no id");

        Call<ResponseBody> call = RetrofitClient.getInstance().getCanal().stopPlayback("22");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    Toast.makeText(context, "Reproducción Pausada.", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void playBack() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        String id = prefs.getString("id", "no id");


        Call<ResponseBody> call = RetrofitClient.getInstance().getCanal().playPlayback("22");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                Log.e("Codigo Reproduccion", response.code() + "");
                if (response.code() == 200) {
                    Toast.makeText(context, "Reproducción Renaudada.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void saveLog(String log) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        String id = prefs.getString("id", "no id");

        Log.e("Log en PlayerS", log);

        Call<ResponseBody> call = RetrofitClient.getInstance().getCanal().saveLog("22", log, "test");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    Log.e("Estados Log", "Log Guardado");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });


    }


}





