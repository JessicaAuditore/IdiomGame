package org.soul.controller;

import org.soul.common.ThreadAdapter;
import org.soul.model.GameApplicationModel;
import org.soul.rule.GameRule;
import org.soul.runnable.ClientThread;
import org.soul.tool.MySocket;

import java.io.*;
import java.net.*;

public class GameApplicationController {

    private GameApplicationModel applicationModel;
    private MySocket mySocket;

    public GameApplicationController(GameApplicationModel applicationModel) {
        this.applicationModel = applicationModel;
        GameRule.applicationModel = applicationModel;
        addEventHandler();
    }

    private void addEventHandler() {
        applicationModel.getRegister().setOnAction(event -> {
            if (!GameRule.match()) {
                return;
            }
            String ip = applicationModel.getIp();
            int port = 8001;
            connectServer(ip, port);
        });

        applicationModel.getSubmit().setOnAction(event -> {
            String answer = applicationModel.getAnswer().getText().trim();
            applicationModel.getSubmit().setDisable(true);
            applicationModel.getTip().setText("");
            applicationModel.getAnswer().setText("");
            applicationModel.getIdiomTextField().setText("");
            try {
                ThreadAdapter.sendMessage(mySocket, answer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void connectServer(String ip, int port) {
        mySocket = new MySocket();
        try {
            mySocket.setSocket(new Socket(ip, port));
            ThreadAdapter.register(mySocket, applicationModel.getId(), applicationModel.getName());
            new ClientThread(mySocket, applicationModel);
        } catch (UnknownHostException e) {
            System.out.println("服务器地址异常" + e.getMessage());
        } catch (IOException e) {
            System.out.println("连接服务器异常" + e.getMessage());
        }
    }
}
