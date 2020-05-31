package com.paul9834.exoplayerchannel.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.paul9834.exoplayerchannel.R;

public class CanalActivity extends AppCompatActivity {

    EditText id;
    Button actividad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_firts);

        id = findViewById(R.id.editText);
        actividad = findViewById(R.id.Enviar);

        checkFirstOpen();


        actividad.setOnClickListener(v -> {
            String texto = id.getText().toString().trim();

            if (TextUtils.isEmpty(texto)) {
                id.setError("Ingrese un ID de canal");
            } else {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(CanalActivity.this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("id", texto);
                editor.apply();

                Intent intent = new Intent(CanalActivity.this, ReproductorActivity.class);
                startActivity(intent);
                finish();
            }

        });
    }

    private void checkFirstOpen() {
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (!isFirstRun) {
            Intent intent = new Intent(CanalActivity.this, ReproductorActivity.class);
            startActivity(intent);
            finish();
        }

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun",
                false).apply();
    }


}
