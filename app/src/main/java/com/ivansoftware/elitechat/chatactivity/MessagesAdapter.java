package com.ivansoftware.elitechat.chatactivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ivansoftware.elitechat.R;

import java.util.List;

public class MessagesAdapter extends ArrayAdapter<MessageItem> {
    private String currentUser;
    public MessagesAdapter(@NonNull Context context, @NonNull List<MessageItem> objects, String currentUser) {
        super(context, R.layout.message_input_item, objects);
        this.currentUser = currentUser;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageItem msgi = getItem(position);
        String sender = msgi.sender;
        String content = msgi.content;

        if (sender.equals(currentUser)) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.message_output_item, null);
            ((TextView) convertView.findViewById(R.id.tv_content))
                    .setText(content);
        } else {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.message_input_item, null);
            ((TextView) convertView.findViewById(R.id.tv_username))
                    .setText(sender);
            ((TextView) convertView.findViewById(R.id.tv_content))
                    .setText(content);
        }
        return convertView;
    }
}
