package com.laoapps;

import com.laoapps.socket.Websocket;

public class Main {

    public static void main(String[] args) {
        new Websocket(8898).start();
    }
}
