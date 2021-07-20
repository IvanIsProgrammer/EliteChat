package com.ivansoftware.elitechat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.network.Server;

public class ServerActivity extends AppCompatActivity implements View.OnClickListener{
    private static Server server;

    private static final int PORT = 8080;

    Button btn_start;
    Button btn_back;

    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_server);

        server = new Server();

        btn_start = findViewById(R.id.btn_start);
        btn_back = findViewById(R.id.btn_back);
        status = findViewById(R.id.tv_status);

        btn_start.setOnClickListener(this);
        btn_back.setOnClickListener(this);

        changeStatus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                start();
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    private void start() {
        server.host(PORT);
        changeStatus();
    }

    private void changeStatus(){
        if (server.isStarted())
            status.setText("запущен");
        else
            status.setText("не запущен");
    }
}