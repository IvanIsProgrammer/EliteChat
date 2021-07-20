package com.network;

import java.io.Serializable;

public class Message implements Serializable{
    public CMD cmd;
    public String arguments;
    public String content;

    public Message(CMD cmd, String arguments, String content) {
        this.cmd = cmd;
        this.arguments = arguments;
        this.content = content;
    }
}
