package com.company;

import java.net.ServerSocket;
import java.net.Socket;

public interface OnConnectionEventListener {
    public void onConnectionEstablished(ServerSocket server);
    public void onConnectionFailed(ServerSocket server);
    public void onNewClientConnected(ServerSocket server, Socket client);
}
