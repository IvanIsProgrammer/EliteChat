package com.network;

import java.io.*;
import java.net.Socket;

public class Client {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private OnNewMessageListener onNewMessageListener = msg->{};
    private OnFailedConnection onFailedConnection = exception->{};
    private OnServerClose onServerClose = exception->{};

    public Client() {
        //ToDO...
    }

    public boolean connect(String address, int port) {
        try {
            this.socket = new Socket(address, port);
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
            new ReadMsg().start();
        } catch (IOException e) {
            onFailedConnection.failedConnection("Server is not available");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void disconnect() {
        try {
            this.socket.close();
            this.in.close();
            this.out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Message msg) {
        new Thread(()->{
            try {
                out.writeObject(msg);
                out.flush();
            } catch (IOException e) {
                disconnect();
                onServerClose.serverClose("Server is closed");
                e.printStackTrace();
            }
        }).start();
    }

    public void authorization(String login, String password) {
        send(new Message(CMD.AUTHORIZATION, login+"@"+password, ""));
    }

    public void registration(String login, String password) {
        send(new Message(CMD.REGISTRATION, login+"@"+password, ""));
    }

    private class ReadMsg extends Thread {
        private boolean run = true;
        @Override
        public void run() {
            while (run) {
                try {
                    Message msg = (Message)in.readObject();
                    onNewMessageListener.newMessage(msg);
                } catch (IOException | ClassNotFoundException e) {
                    run = false;
                    disconnect();
                    onServerClose.serverClose("Servers I/O Streams is closed");
                    e.printStackTrace();
                }
            }
        }
    }

    public interface OnNewMessageListener {
        void newMessage(Message msg);
    }

    public interface OnFailedConnection {
        void failedConnection(String exception);
    }

    public interface OnServerClose {
        void serverClose(String exception);
    }


    public void setOnNewMessageListener(OnNewMessageListener onNewMessageListener) {
        this.onNewMessageListener = onNewMessageListener;
    }

    public void setOnFailedConnection(OnFailedConnection onFailedConnection) {
        this.onFailedConnection = onFailedConnection;
    }

    public void setOnServerClose(OnServerClose onServerClose) {
        this.onServerClose = onServerClose;
    }

}
