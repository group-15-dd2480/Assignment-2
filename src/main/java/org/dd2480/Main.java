package org.dd2480;

public class Main {

    // What interface we will listen on, default to localhost
    static final String BIND_IP = System.getenv().getOrDefault("BIND_IP", "127.0.0.1");
    // What port we will bind on
    static final int PORT = Integer.parseInt(System.getenv().getOrDefault("PORT", "3333"));

    public static void main(String[] args) {
        App app = new App(BIND_IP, PORT);
        app.start();
    }

}
