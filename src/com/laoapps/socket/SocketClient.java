package com.laoapps.socket;

import com.laoapps.utils.MyCommon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketClient {

    public SocketClient() {
    }

    public String sendAndReceive(String request) throws IOException {
        Socket socket = new Socket("localhost", 5002);
        try (PrintWriter output = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

            output.println(request);
            String response = input.readLine();
            MyCommon.printMessage("response from user manager: " + response);
            socket.close();

            return response;
        }
    }

}
