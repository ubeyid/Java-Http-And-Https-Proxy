package com.company;

public class Utils {
    public static int SERVER_PORT_TO_LISTEN=8888;
    public static String PROXY_HOST="194.163.43.216";
    public static int PROXY_PORT=3128;
    public static String USER_AGENT="Efbiay/1.0";
    public static String CRLF="\r\n";
    public static String PROXY_CONNECTION_OK="200 Connection established"+CRLF+
                                             "Proxy-Agent: Efbiay/1.0"+CRLF+
                                             "Proxy-Connection: Keep-Alive"+CRLF+
                                             "Connection: Keep-Alive"+CRLF+
                                             CRLF;
    public static String BAD_REQUEST="HTTP/1.1 400 Bad request"+CRLF+
                                           "Proxy-Connection: Close"+CRLF+
                                           CRLF;
    public static String GET_REQUEST_TO_PROXY="GET http://"+PROXY_HOST+":"+PROXY_PORT+" HTTP/1.1"+CRLF+
                                              "Host: "+PROXY_HOST+":"+PROXY_PORT+CRLF+
                                              "User-Agent: "+USER_AGENT+CRLF+
                                              CRLF;
    public static String CONNECT_REQUEST_TO_PROXY="CONNECT "+PROXY_HOST+":"+PROXY_PORT+" HTTP/1.1"+CRLF+
            "Proxy-Connection: Keep-Alive"+CRLF+
            "Connection: Keep-Alive"+CRLF+
            "Host: "+PROXY_HOST+":"+PROXY_PORT+CRLF+
            "User-Agent: "+USER_AGENT+CRLF+
            CRLF;

}
