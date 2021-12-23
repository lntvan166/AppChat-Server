package com.app.util;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * com.app.util
 * Create by Le Nguyen Tu Van
 * Date 12/18/2021 - 8:03 PM
 * Description: ...
 */
public class AppUtil {

    public static List<User> userList;
    public static String user;

    // read user information from text file
    // format: username`password`name
    public static List<User> readUser() throws IOException {
        List<User> userList = new ArrayList<>();

        BufferedReader fileIn = new BufferedReader(new FileReader("userData.txt"));
        String line;

        while ((line = fileIn.readLine()) != null) {
            String[] buffer = line.split("`");
            String username = buffer[0];
            String password = buffer[1];

            User tempUser = new User(username, password);
            userList.add(tempUser);

        }

        fileIn.close();

        return userList;
    }

    // write user information to text file
    // format: username`password`name
    public static void writeUser(List<User> userList) throws IOException {
        BufferedWriter fileOut = new BufferedWriter(new FileWriter("userData.txt"));

        for (User user : userList) {
            fileOut.write(user.toString());
            fileOut.newLine();
        }

        fileOut.close();
    }

    public static boolean isContainUser(String username) {
        boolean isContain = false;
        for (User user : userList) {
            if (Objects.equals(user.getUsername(), username)) {
                isContain = true;
                break;
            }
        }

        return isContain;
    }

    public static boolean authUser(String username, String password) {
        boolean isValid = false;

        if (isContainUser(username)) {
            for (User user : userList) {
                if (Objects.equals(user.getUsername(), username)) {
                    isValid = Objects.equals(user.getPassword(), password);
                }
            }
        }

        return isValid;

    }

//    public static String getNameByUsername(String username) {
//        String name = "";
//        for(User user: userList) {
//            if(Objects.equals(user.getUsername(), username)) {
//                name = user.getName();
//            }
//        }
//
//        return name;
//    }

    public static String getFileExtension(String filename) {
        int i = filename.lastIndexOf(".");

        if (i > 0) {
            return filename.substring(i + 1);
        } else {
            return "No extension found";
        }
    }


}
