package com.ivansoftware.elitechat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.network.Client;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private final int PORT = 8080;

    private final String IP = "IP";

    public static Client client = new Client();
    private EditText et_ip;
    private Button btn_connect;
    private Button btn_controlServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_ip = findViewById(R.id.et_ip);
        btn_connect = findViewById(R.id.btn_connect);
        btn_controlServer = findViewById(R.id.btn_controlServer);

        btn_connect.setOnClickListener(this);
        btn_controlServer.setOnClickListener(this);

        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        et_ip.setText(sPref.getString(IP, ""));


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connect:
                String ip = et_ip.getText().toString();
                connect(ip);
                break;
            case R.id.btn_controlServer:
                Intent intent = new Intent(this, ServerActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void connect(String ip) {
        Handler handler = new Handler();
        new Thread(()->{
            if (client.connect(ip, PORT)) {
                SharedPreferences sPref = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = sPref.edit();
                editor.putString(IP, ip);
                editor.apply();

                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            } else {
                handler.post(() -> runOnUiThread(() -> {
                    Toast.makeText(this, "Connection is failed", Toast.LENGTH_SHORT).show();
                }));
            }
        }).start();
    }
}