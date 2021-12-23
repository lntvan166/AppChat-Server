package com.app.server;

import com.app.util.AppUtil;
import com.app.util.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
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
//    private BufferedReader bufferedReader;
//    private BufferedWriter bufferedWriter;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            this.dataInputStream = new DataInputStream(socket.getInputStream());

            String method = dataInputStream.readUTF();
            String username = dataInputStream.readUTF();
            String password = dataInputStream.readUTF();

            if (method.equals("auth")) {
                if (AppUtil.authUser(username, password)) {
                    dataOutputStream.writeUTF("true");
                    this.clientUsername = dataInputStream.readUTF();
                    clientHandlers.add(this);

                    refreshOnlineUser();
                } else {
                    dataOutputStream.writeUTF("false");
                    closeEverything(socket, dataInputStream, dataOutputStream);
                }
            } else {
                if(AppUtil.isContainUser(username)) {
                    dataOutputStream.writeUTF("exist");
                } else {
                    AppUtil.userList.add(new User(username, password));
                    AppUtil.writeUser(AppUtil.userList);
                    dataOutputStream.writeUTF("success");
                }
                closeEverything(socket, dataInputStream, dataOutputStream);
            }
        } catch (IOException e) {
            closeEverything(socket, dataInputStream, dataOutputStream);
        }
    }

    @Override
    public void run() {
        String message;

        while (socket.isConnected()) {
            try {
                message = dataInputStream.readUTF();
                System.out.println("Receive from " + clientUsername + ": " + message);
                if (Objects.equals(message, "File")) {
                    receiveAndSendFile();
                } else {
                    sendMessage(message);
                }

            } catch (IOException e) {
                closeEverything(socket, dataInputStream, dataOutputStream);
                break;
            }
        }
    }

    public void receiveAndSendFile() throws IOException {
        System.out.println("---receive:");
        String userTo = dataInputStream.readUTF();
        System.out.println(userTo);

        int fileNameLength = dataInputStream.readInt();
        System.out.println(fileNameLength);

        if (fileNameLength > 0) {
            byte[] fileNameBytes = new byte[fileNameLength];
            dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
            String filename = new String(fileNameBytes);
            System.out.println(Arrays.toString(fileNameBytes));

            int fileContentLength = dataInputStream.readInt();
            System.out.println(fileContentLength);

            if(fileContentLength > 0) {
                byte[] fileContentBytes = new byte[fileContentLength];
                dataInputStream.readFully(fileContentBytes, 0, fileContentLength);
                System.out.println(Arrays.toString(fileContentBytes));



                for (ClientHandler clientHandler : clientHandlers) {
                    if (clientHandler.clientUsername.equals(userTo)) {
                        clientHandler.dataOutputStream.writeUTF("File");
                        clientHandler.dataOutputStream.flush();
                        clientHandler.dataOutputStream.writeUTF(clientUsername);
                        clientHandler.dataOutputStream.flush();

                        clientHandler.dataOutputStream.writeInt(fileNameBytes.length);
                        clientHandler.dataOutputStream.write(fileNameBytes);

                        clientHandler.dataOutputStream.writeInt(fileContentBytes.length);
                        clientHandler.dataOutputStream.write(fileContentBytes);

                        System.out.println("--------send:");
                        System.out.println("File");
                        System.out.println(userTo);
                        System.out.println(fileNameBytes.length);
                        System.out.println(Arrays.toString(fileNameBytes));
                        System.out.println(fileContentBytes.length);
                        System.out.println(Arrays.toString(fileContentBytes));

                    }
                }

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
        if (clientHandlers.size() == 1) messageToSend += "noOneOnline";

        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (clientHandler.clientUsername.equals(userTo)) {
                    clientHandler.dataOutputStream.writeUTF(messageToSend);
                    System.out.println("Send: " + messageToSend);
                    clientHandler.dataOutputStream.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, dataInputStream, dataOutputStream);
            }
        }
    }

    public String typeHandle(String type, String userFrom, String userTo, String message) {
        StringBuilder messageAfterHandle = new StringBuilder();
        if (Objects.equals(type, "message")) {
            messageAfterHandle = new StringBuilder(type + "@#@" + userFrom + "@#@" + message);
        }
        if (Objects.equals(type, "GetUserOnline")) {
            messageAfterHandle.append(type).append("@#@").append(userFrom).append("@#@");
            for (ClientHandler clientHandler : clientHandlers) {
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    messageAfterHandle.append(clientHandler.clientUsername).append(" ");
                }
            }
        }
        return messageAfterHandle.toString();
    }

    public void closeEverything(Socket socket, DataInputStream bufferedWriter, DataOutputStream bufferedReader) {
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
                clientHandler.dataOutputStream.writeUTF("OnlineUserChange");
                clientHandler.dataOutputStream.flush();
            } catch (IOException e) {
                closeEverything(socket, dataInputStream, dataOutputStream);
            }
        }
    }
}