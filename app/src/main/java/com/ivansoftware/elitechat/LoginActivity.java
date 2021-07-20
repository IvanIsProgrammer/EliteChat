package com.ivansoftware.elitechat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.network.CMD;
import com.network.Client;

public class LoginActivity extends AppCompatActivity implements OnClickListener{
    private final String LOGIN = "LOGIN";

    Client client = MainActivity.client;

    EditText et_login;
    EditText et_password;
    Button btn_login;
    Button btn_register;

    SharedPreferences sPref;

    @Override
    protected void onResume() {
        setClientOnNewMessageListener();
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_login = findViewById(R.id.et_login);
        et_password = findViewById(R.id.et_password);

        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);

        sPref = getPreferences(MODE_PRIVATE);
        et_login.setText(sPref.getString(LOGIN, ""));

       setClientOnNewMessageListener();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        String login = et_login.getText().toString();
        String password = et_password.getText().toString();
        switch (id) {
            case R.id.btn_login:
                if (login.contains("@") || login.isEmpty()) {
                    Toast.makeText(this, "Логин не может быть пустым и содержать знак @", Toast.LENGTH_SHORT).show();
                } else if (password.contains("@") || password.isEmpty()) {
                    Toast.makeText(this, "Пароль не может быть пустым и содержать знак @", Toast.LENGTH_SHORT).show();
                } else {
                    client.authorization(login, password);
                }
                break;
            case R.id.btn_register:
                startActivity(new Intent(this, RegistrationActivity.class));
                break;
        }
    }

    private void setClientOnNewMessageListener() {
        Handler handler = new Handler();
        client.setOnNewMessageListener(msg->{
            String content = msg.content;
            CMD cmd = msg.cmd;
            if (cmd == CMD.AUTHORIZATION) {
                if (content.equals("true")) {
                    handler.post(() -> runOnUiThread(() -> {
                        SharedPreferences.Editor editor = sPref.edit();
                        editor.putString(LOGIN, et_login.getText().toString());
                        editor.apply();
                        Toast.makeText(this, "Вы успешно авторизировались", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(this, ChatActivity.class);
                        intent.putExtra("USERNAME", et_login.getText().toString());
                        startActivity(intent);
                        finish();
                    }));
                } else {
                    handler.post(() -> runOnUiThread(() -> {
                        Toast.makeText(this, "Неверное имя или пароль пользователя", Toast.LENGTH_SHORT).show();
                    }));
                }
            }
        });
    }

}