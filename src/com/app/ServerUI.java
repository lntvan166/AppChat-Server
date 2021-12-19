package com.app;

import com.app.server.ClientHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * com.app
 * Create by Le Nguyen Tu Van
 * Date 12/19/2021 - 10:53 PM
 * Description: ...
 */
public class ServerUI {
    private JFrame frameMain;
    private JPanel panelMain;
    private JLabel countLabel;
    private JButton cancelButton;

    public ServerUI() {
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame cancelFrame = new JFrame("EXIT");
                if (JOptionPane.showConfirmDialog(cancelFrame, "Confirm if you want to exit", "EXIT",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_NO_OPTION) {
                    System.exit(0);
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
        refresh();
    }

    public void catchException(IOException e) {
        JOptionPane.showMessageDialog(null, e);
    }

    public void refresh() {
        int count = ClientHandler.clientHandlers.size();
        countLabel.setText("Number of online users: " + count);
    }
}
