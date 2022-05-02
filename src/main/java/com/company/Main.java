package com.company;

public class Main{

    public static void main(String[] args) {

     System.setProperty("javax.net.ssl.keyStore", "za.store");
     System.setProperty("javax.net.ssl.keyStorePassword", "ubeyid");
      new Thread(new ProxyServer(Utils.SERVER_PORT_TO_LISTEN)).start();

    }

}
