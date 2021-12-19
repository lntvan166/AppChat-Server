package com.app.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

/**
 * com.app.server
 * Create by Le Nguyen Tu Van
 * Date 12/19/2021 - 3:11 PM
 * Description: ...
 */
public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);

            refreshOnlineUser();

        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    @Override
    public void run() {
        String message;

        while (socket.isConnected()) {
            try {
                message = bufferedReader.readLine();
                System.out.println("Receive: " + message);
                sendMessage(message);
            } catch (IOException e) {
                closeEverything(socket, bufferedWriter, bufferedReader);
                break;
            }
        }
    }

    public void sendMessage(String message) {
        String[] buffer = message.split("@#@"); // format message: type@#@userFrom@#@userTo@#@message
        String type = buffer[0];
        String userFrom = buffer[1];
        String userTo = buffer[2];
        String bodyMessage = buffer[3];


        String messageToSend = typeHandle(type, userFrom, userTo, bodyMessage);
        if(clientHandlers.size()==1)messageToSend += "noOneOnline";

        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (clientHandler.clientUsername.equals(userTo)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    System.out.println("Send: " + messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedWriter, bufferedReader);
            }
        }
    }

    public String typeHandle(String type, String userFrom, String userTo, String message) {
        StringBuilder messageAfterHandle = new StringBuilder();
        if (Objects.equals(type, "message")) {
            messageAfterHandle = new StringBuilder(type + "@#@" + userFrom + "@#@" + message);
        }
        if(Objects.equals(type, "GetUserOnline")) {
            messageAfterHandle.append(type).append("@#@").append(userFrom).append("@#@");
            for(ClientHandler clientHandler : clientHandlers) {
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    messageAfterHandle.append(clientHandler.clientUsername).append(" ");
                }
            }
        }
        return messageAfterHandle.toString();
    }

    public void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        removeClientHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        refreshOnlineUser();
        ServerChat.serverUI.refresh();
    }

    public void refreshOnlineUser() {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                clientHandler.bufferedWriter.write("OnlineUserChange");
                clientHandler.bufferedWriter.newLine();
                clientHandler.bufferedWriter.flush();
            } catch (IOException e) {
                closeEverything(socket, bufferedWriter, bufferedReader);
            }
        }
    }
}