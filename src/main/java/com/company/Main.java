package com.company;

public class Main{

    public static void main(String[] args) {
	// write your code
      new Thread(new ProxyServer(Utils.SERVER_PORT_TO_LISTEN)).start();

    }

}
