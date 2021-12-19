package com.app.server;

import com.app.Main;
import com.app.ServerUI;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * com.app.server
 * Create by Le Nguyen Tu Van
 * Date 12/18/2021 - 10:07 PM
 * Description: ...
 */
public class ServerChat {
    private ServerSocket serverSocket;
    public static ServerUI serverUI;

    public ServerChat(ServerSocket serverSocket, ServerUI serverUI) {
        this.serverSocket = serverSocket;
        ServerChat.serverUI = serverUI;
    }

    public void startServer() {
        try {
            while(!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected!");


                ClientHandler clientHandler = new ClientHandler(socket);
                ServerChat.serverUI.refresh();

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
