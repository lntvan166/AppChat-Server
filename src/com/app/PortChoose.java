package com.app;

import com.app.server.ServerChat;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * com.app
 * Create by Le Nguyen Tu Van
 * Date 12/20/2021 - 4:42 PM
 * Description: ...
 */
public class PortChoose {
    private JFrame frameMain;
    private JPanel panelMain;
    private JTextField textField1;
    private JButton submitButton;

    public PortChoose() {
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int port = -1;
                try {
                    port = Integer.parseInt(textField1.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Port must be a number!");
                }

                if (port > 0) {
                    ServerSocket serverSocket = null;
                    try {
                        serverSocket = new ServerSocket(port);

                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null,
                                "Choose another port because this port already in use");
                    }

                    ServerUI serverUI = new ServerUI(port);
                    serverUI.start();

                    ServerChat serverChat = new ServerChat(serverSocket, serverUI);


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            serverChat.startServer();
                        }
                    }).start();

                    frameMain.dispose();
                }
                else {
                    JOptionPane.showMessageDialog(null, "Port invalid");
                }

            }
        });
    }

    public void start() {
        frameMain = new JFrame("Server chat");
        frameMain.setContentPane(panelMain);
        frameMain.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frameMain.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JFrame cancelFrame = new JFrame("EXIT");
                if (JOptionPane.showConfirmDialog(cancelFrame, "Confirm if you want to exit", "EXIT",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_NO_OPTION) {
                    System.exit(0);
                }
            }
        });
        frameMain.setLocationRelativeTo(null);
        frameMain.pack();
        frameMain.setVisible(true);
    }
}
