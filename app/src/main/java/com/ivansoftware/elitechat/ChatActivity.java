package com.ivansoftware.elitechat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.ivansoftware.elitechat.chatactivity.MessageItem;
import com.ivansoftware.elitechat.chatactivity.MessagesAdapter;
import com.network.CMD;
import com.network.Client;
import com.network.Message;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener{
    Client client;

    ListView lv_messages;
    EditText et_content;
    ImageButton btn_send;

    MessagesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        client = MainActivity.client;

        et_content = findViewById(R.id.et_content);
        btn_send = findViewById(R.id.btn_send);
        lv_messages = findViewById(R.id.lv_messages);

        btn_send.setOnClickListener(this);

        ArrayList<MessageItem> messageItemList = new ArrayList<>();
        adapter = new MessagesAdapter(this, messageItemList, getIntent().getStringExtra("USERNAME"));
        lv_messages.setAdapter(adapter);


        Handler handler = new Handler();
        client.setOnNewMessageListener(msg -> {
            String sender = msg.arguments.split("@")[0];
            String content = msg.content;
            handler.post(() -> runOnUiThread(() -> {
                MessageItem msgi = new MessageItem();
                msgi.content = content;
                msgi.sender = sender;
                adapter.add(msgi);
                adapter.notifyDataSetChanged();
            }));
        });

        client.setOnFailedConnection(exception -> {
            handler.post(()->{
                Toast.makeText(this, "Сервер закрыт!", Toast.LENGTH_SHORT).show();
            });
            finish();
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                send();
                break;
        }
    }

    private void send() {
        String content = et_content.getText().toString();
        et_content.setText("");
        client.send(new Message(CMD.MESSAGE, "", content));
    }
}