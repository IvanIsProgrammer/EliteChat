package com.ivansoftware.elitechat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.network.CMD;
import com.network.Client;

public class RegistrationActivity extends AppCompatActivity implements OnClickListener {
    Client client = MainActivity.client;

    EditText et_login;
    EditText et_password;
    EditText et_passwordConfirm;
    Button btn_back;
    Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        et_login = findViewById(R.id.et_login);
        et_password = findViewById(R.id.et_password);
        et_passwordConfirm = findViewById(R.id.et_passwordConfirm);

        btn_back = findViewById(R.id.btn_back);
        btn_register = findViewById(R.id.btn_register);
        btn_back.setOnClickListener(this);
        btn_register.setOnClickListener(this);

        Handler handler = new Handler();
        client.setOnNewMessageListener(msg -> {
            String content = msg.content;
            CMD cmd = msg.cmd;
            if (cmd == CMD.REGISTRATION) {
                if (content.equals("true")) {
                    handler.post(() -> runOnUiThread(() -> {
                        Toast.makeText(this, "Вы успешно зарегистрировались", Toast.LENGTH_SHORT).show();
                        finish();
                    }));
                } else {
                    handler.post(() -> runOnUiThread(() -> {
                        Toast.makeText(this, "Данное имя пользователя уже используется", Toast.LENGTH_SHORT).show();
                    }));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        String login = et_login.getText().toString();
        String password = et_password.getText().toString();
        String passwordConfirm = et_passwordConfirm.getText().toString();
        switch (id) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_register:
                if (login.contains("@") || login.isEmpty()) {
                    Toast.makeText(this, "Логин не может быть пустым и содержать знак @", Toast.LENGTH_SHORT).show();
                } else if (password.contains("@") || password.isEmpty()) {
                    Toast.makeText(this, "Пароль не может быть пустым и содержать знак @", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(passwordConfirm)) {
                    Toast.makeText(this, "Введённые пароли не совпадают", Toast.LENGTH_SHORT).show();
                } else {
                    client.registration(login, password);
                }
                break;
        }
    }
}