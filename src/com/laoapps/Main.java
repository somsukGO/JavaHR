package com.laoapps;

import com.laoapps.websocker.WSServer;

public class Main {

    public static void main(String[] args) {
        new WSServer(8899).start();
    }
}
