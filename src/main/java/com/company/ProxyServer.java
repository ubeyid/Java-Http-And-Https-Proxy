package com.company;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ProxyServer implements Runnable,OnConnectionEventListener{
    private int port;
    private ServerSocket server=null;
    public ProxyServer(int port)  {
        this.port=port;
    }


    @Override
    public void run() {
        listen();
    }



    public void listen(){
        try {
            ServerSocket server=new ServerSocket(port);
            onConnectionEstablished(server);

            while(server.isBound() && !server.isClosed()){
                Socket client=server.accept();
                onNewClientConnected(server,client);
                new Thread(new Client(client)).start();

            }
        } catch (IOException e) {
            onConnectionFailed(server);
            e.printStackTrace();
        }
    }

    @Override
    public void onNewClientConnected(ServerSocket server, Socket client) {
     System.out.println("New Request Received :)");
    }
    @Override
    public void onConnectionEstablished(ServerSocket server) {
         System.out.println("Proxy server is ready to accept connections!!");
         System.out.println("Proxy server is running on Port:"+Utils.SERVER_PORT_TO_LISTEN);
    }


    @Override
    public void onConnectionFailed(ServerSocket server) {
        System.out.println("Something went wrong during listening port for Proxy server:(");
    }
}
