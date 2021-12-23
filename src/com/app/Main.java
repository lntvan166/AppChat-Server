package com.app;

import com.app.server.ServerChat;
import com.app.util.AppUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * com.app
 * Create by Le Nguyen Tu Van
 * Date 12/19/2021 - 10:52 PM
 * Description: ...
 */
public class Main {
    public static void main(String[] args) {
        try {
            AppUtil.userList = AppUtil.readUser();
        } catch (IOException e) {
            AppUtil.userList = new ArrayList<>();
        }
        PortChoose app = new PortChoose();
        app.start();
    }
}
