package com.company;


import java.io.*;
import java.net.*;

import java.nio.charset.StandardCharsets;



public class Client extends Thread{
    private final Socket client;
    private Socket server;
    private InputStream clientIn,serverIn;
    private OutputStream clientOut,serverOut;
    private String host=null;//host that we make request
    private int port=0;//port that we connect
    private BufferedReader buffer;
    private String version;
    public Client(Socket client){
        this.client=client;

        try {
            clientIn=client.getInputStream();
            buffer=new BufferedReader(new InputStreamReader(clientIn));
            clientOut=client.getOutputStream();
        } catch (IOException e) {

            e.printStackTrace();
        }

}

    @Override
    public void run() {
        super.run();
        try {
            byte[] rawrequest=new byte[1024];

            int size=clientIn.read(rawrequest);
            boolean checkHostAndPort=findHostAndPort(rawrequest,size);
            if(!checkHostAndPort){
                System.out.println("Could not find Host and port number!!");
                client.close();
                this.interrupt();
            }

            if((version=checkIfSecure(rawrequest,size)) != null){
                String connectionOk=(version+" "+Utils.PROXY_CONNECTION_OK);
                clientOut.write(connectionOk.getBytes(StandardCharsets.UTF_8));
                server=new Socket(host,port);

                serverIn=server.getInputStream();
                serverOut=server.getOutputStream();

            }else{
                byte[] request=parseRequest(rawrequest,size);
                server=new Socket(host,port);

                serverIn=server.getInputStream();
                serverOut=server.getOutputStream();
                serverOut.write(request);
            }
            relay();

        } catch (IOException e) {
            //e.printStackTrace();
            try {
                client.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            this.interrupt();
        }finally {
               quickClose(client);
               quickClose(server);
               this.interrupt();
               System.out.println("One of requests successfully completed..\n");
        }

    }

    public void relay() throws IOException {

        Thread thread=new Thread(() -> {
            while (!client.isClosed() && !server.isClosed() && !client.isInputShutdown() && !server.isOutputShutdown()){
                byte[] request = new byte[1024];
                int size;
                try {
                     size = clientIn.read(request);

                    if (size > 0) {
                        if(version == null){
                            byte[] parsedReq=parseRequest(request,size);
                            String req = new String(parsedReq,0,parsedReq.length);
                            System.out.println("Req:" + req);
                            serverOut.write(parsedReq,0,parsedReq.length);
                            serverOut.flush();
                        }else{
                            String req = new String(request,0,size);
                            System.out.println("Req:" + req);
                            serverOut.write(request,0,size);
                            serverOut.flush();
                        }

                    }
                    if (size < 0) {
                        client.shutdownInput();
                        server.shutdownOutput();
                        break;
                    }

                } catch (IOException e) {
                    // e.printStackTrace();
                 break;
                }

            }
        });
        thread.start();

        byte[] response = new byte[4096];
        int size;
        while (!client.isClosed() && !server.isClosed() && !client.isOutputShutdown() && !server.isInputShutdown()){

            try {

                size=serverIn.read(response);
                if(size > 0){

                    String res=new String(response,0,size);
                    System.out.println("Response:"+res);
                    clientOut.write(response,0,size);
                    clientOut.flush();
}
                if (size < 0){
                    server.shutdownInput();
                    client.shutdownOutput();
                    break;
                }
            } catch (IOException e) {
                //e.printStackTrace();
                break;

            }

        }
        thread.interrupt();

    }

    public void quickClose(Socket socket){
       if(socket != null && !socket.isClosed()){
           if(!socket.isOutputShutdown()){
               try {
                   socket.shutdownOutput();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
           if(!socket.isInputShutdown()){
               try {
                   socket.shutdownInput();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
           try {
               socket.close();
           } catch (IOException e) {
               e.printStackTrace();
           }
       }

    }

    public String checkIfSecure(byte[] packet, int size){
        String req="CONNECT facebook.com:443 HTTP/1.1\r\n";
        if(size == -1){
            return null;
        }
        String request=new String(packet,0,size,StandardCharsets.UTF_8);
        if(request.startsWith("CONNECT")){
            String[] headers=request.split("\r\n");
            String firstLine=headers[0];
            String[] linePieces=firstLine.split(" ");
            String version=linePieces[2];//HTTP version
            return version;

        }
        return null;
    }
    public byte[] parseRequest(byte[] packet,int length){

        String requestString=new String(packet,0,length);
        String modifiedRequest=requestString.replaceFirst("http(s)?://[0-9a-z.]{1,500}(/)?","/");
        modifiedRequest=modifiedRequest.replaceFirst("Proxy-Connection: keep-alive\r\n","Connection: keep-alive\r\n");

        return modifiedRequest.getBytes();//return new request as byte array

    }


    public  boolean findHostAndPort(byte[] packet,int length) throws UnknownHostException {
        boolean state=false;
        if(length == -1){
            return false;
        }
        String packetString=new String(packet,0,length, StandardCharsets.UTF_8);
        String[] requestLines=packetString.split("\r\n");
        for (String requestLine : requestLines) {

            if (requestLine.startsWith("Host")) {
                String[] connection = requestLine.split(":");
                host = connection[1].trim();//String raw
                host = InetAddress.getByName(host).getHostAddress();
                if(connection.length == 2) {

                    port = 80;

                } else {

                    String port_s = connection[2];//raw string port
                    port = Integer.parseInt(port_s);
                }
                state = true;
                break;

            }
        }
      return state;
    }


}
