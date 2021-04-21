package com.laoapps.handler;

import com.google.gson.JsonObject;
import com.laoapps.socket.SocketClient;

import java.io.IOException;

public class UsersHandler {
    private UsersHandler() {
    }

    private static UsersHandler usersHandler = null;

    public static UsersHandler getInstance() {
        if (usersHandler == null) {
            usersHandler = new UsersHandler();
        }

        return usersHandler;
    }

    public String register(JsonObject registerRequest) throws IOException {
        return new SocketClient().sendAndReceive(registerRequest);
    }

    public String login(JsonObject loginRequest) throws IOException {
        return new SocketClient().sendAndReceive(loginRequest);
    }

}
