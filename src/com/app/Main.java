package com.app;

import com.app.server.ServerChat;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * com.app
 * Create by Le Nguyen Tu Van
 * Date 12/19/2021 - 10:52 PM
 * Description: ...
 */
public class Main {
    public static void main(String[] args) {
        PortChoose app = new PortChoose();
        app.start();
    }
}
