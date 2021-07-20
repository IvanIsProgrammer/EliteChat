package com.network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server {
    /*Обеспечивает работу сервисов:
    * 1) Отслеживание новых подключений ... NewConnectionListener
    * 2) Управление авторизацией пользователя ... LobbyUser
    * 3) Управление сообщениями для пользователя ... ServiceUser*/

    private ServerSocket serverSocket;
    private boolean started = false;

    private final LinkedList<User> users = new LinkedList<>();
    private final LinkedList<LobbyUser> lobby = new LinkedList<>();
    private final LinkedList<ServiceUser> services = new LinkedList<>();
    private final LinkedList<Message> history = new LinkedList<>();

    private OnClientConnected onClientConnected = () -> {};
    private OnClientDisconnected onClientDisconnected = () -> {};
    private OnClientRegistration onClientRegistration = () -> {};
    private OnClientAuthorization onClientAuthorization = () -> {};
    private OnServerStarted onServerStarted = () -> {};
    private OnServerStopped onServerStopped = () -> {};

    private Thread NewConnectListenerThread;



    public void stop() {
        started = false;
        onServerStopped.serverStop();

    }

    public void host(int port) {
        if (started)
            stop();
        try {
            started = true;
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            stop();
        }

        NewConnectListenerThread = new Thread(this::NewConnectListener);
        NewConnectListenerThread.start();
        onServerStarted.serverStart();
    }

    public boolean isStarted() {
        return started;
    }

    private void NewConnectListener() {
        while(started) {
            try {
                Socket socket = serverSocket.accept();
                lobby.add(new LobbyUser(new MySocket(socket)));
                this.onClientConnected.clientConnected();
            } catch (IOException e) {
                stop();
            }
        }
    }

    private void send(MySocket mySocket, Message msg) {
        new Thread(()->{
            try {
                mySocket.out.writeObject(msg);
                mySocket.out.flush();
            } catch (IOException ignored) {}
        }).start();
    }

    private User registration(String username, String password) {
        for (User user: users) {
            if (user.name.equals(username))
                return null;
        }
        User user = new User();
        user.name = username;
        user.password = password;
        return user;
    }

    private User authorization(String username, String password) {
        for (User user: users) {
            if (user.name.equals(username) && user.password.equals(password)) {
                return user;
            }
        }
        return null;
    }


    class ServiceUser extends Thread{
        private final MySocket mySocket;

        User user;

        ServiceUser(MySocket mySocket, User user){
            this.mySocket = mySocket;
            this.user = user;
            start();
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Message msg = (Message)mySocket.in.readObject();
                    String sender = user.name;
                    String  content = msg.content;
                    Message msgOutput = new Message(CMD.MESSAGE, sender, content);
                    history.add(msg);
                    for (ServiceUser service : services) {
                        send(service.mySocket, msgOutput);
                    }
                }
            } catch (NullPointerException | ClassNotFoundException | IOException e) {
                try {
                    mySocket.socket.close();
                    onClientDisconnected.clientDisconnected();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } finally {
                    services.remove(this);
                }
            }
        }
    }


    class LobbyUser extends Thread{
        private final MySocket mySocket;

        LobbyUser(MySocket mySocket){
            this.mySocket = mySocket;
            start();
        }

        @Override
        public void run() {
            try {
                onClientConnected.clientConnected();
                while (true) {
                    Message msg = (Message)mySocket.in.readObject();
                    CMD cmd = msg.cmd;
                    String[] arguments = msg.arguments.split("@");
                    if (cmd == CMD.REGISTRATION) {
                        User user = registration(arguments[0], arguments[1]);
                        if (user == null) {
                            send(mySocket, new Message(CMD.REGISTRATION, "", "false"));
                        } else {
                            users.add(user);
                            send(mySocket, new Message(CMD.REGISTRATION, "", "true"));
                            onClientRegistration.clientRegistration();
                        }
                    } else if (cmd == CMD.AUTHORIZATION) {
                        User user = authorization(arguments[0], arguments[1]);
                        if (user == null) {
                            send(mySocket, new Message(CMD.AUTHORIZATION, "", "false"));
                        } else {
                            send(mySocket, new Message(CMD.AUTHORIZATION, "", "true"));
                            services.add(new ServiceUser(mySocket, user));
                            onClientAuthorization.clientAuthorization();
                            break;
                        }
                    }
                }
            } catch (NullPointerException | ClassNotFoundException | IOException e) {
                try {
                    mySocket.socket.close();
                } catch (IOException ignored) {
                } finally {
                    onClientDisconnected.clientDisconnected();
                    lobby.remove(this);
                }
            }
        }
    }


    public interface OnClientConnected {
        void clientConnected();
    }

    public interface OnClientRegistration {
        void clientRegistration();
    }

    public interface OnClientAuthorization {
        void clientAuthorization();
    }

    public interface OnClientDisconnected {
        void clientDisconnected();
    }

    public interface OnServerStarted {
        void serverStart();
    }

    public interface OnServerStopped {
        void serverStop();
    }


    public void setOnClientConnected(OnClientConnected onClientConnected) {
        this.onClientConnected = onClientConnected;
    }

    public void setOnClientDisconnected(OnClientDisconnected onClientDisconnected) {
        this.onClientDisconnected = onClientDisconnected;
    }

    public void setOnClientRegistration(OnClientRegistration onClientRegistration) {
        this.onClientRegistration = onClientRegistration;
    }

    public void setOnClientAuthorization(OnClientAuthorization onClientAuthorization) {
        this.onClientAuthorization = onClientAuthorization;
    }

    public void setOnServerStarted(OnServerStarted onServerStarted) {
        this.onServerStarted = onServerStarted;
    }

    public void setOnServerStopped(OnServerStopped onServerStopped) {
        this.onServerStopped = onServerStopped;
    }
}
