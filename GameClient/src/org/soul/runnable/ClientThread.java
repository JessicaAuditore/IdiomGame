package org.soul.runnable;

import javafx.scene.paint.Color;
import org.soul.common.ThreadAdapter;
import org.soul.model.GameApplicationModel;
import org.soul.tool.MySocket;
import org.soul.tool.Tokenizer;

import java.io.IOException;

public class ClientThread implements Runnable {

    private MySocket mySocket;
    private GameApplicationModel applicationModel;

    public ClientThread(MySocket mySocket, GameApplicationModel applicationModel) {
        this.mySocket = mySocket;
        this.applicationModel = applicationModel;
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                String message = ThreadAdapter.getMessage(mySocket);
                Tokenizer tokens = new Tokenizer(message, "@");
                String messageType = tokens.nextToken();
                switch (messageType) {
                    case "REGISTERSUCCESS": {
                        String tip = tokens.nextToken();
                        applicationModel.getProcess().setText(applicationModel.getProcess().getText() + "\n" + tip);
                        applicationModel.getRegister().setDisable(true);
                        break;
                    }
                    case "REGISTERFAIL": {
                        applicationModel.getTip().setText("已连接服务器");
                        String tip = tokens.nextToken();
                        applicationModel.getProcess().setText(applicationModel.getProcess().getText() + "\n" + tip);
                        break;
                    }
                    case "LOGIN": {
                        String name = tokens.nextToken();
                        applicationModel.getProcess().setText(applicationModel.getProcess().getText() + "\n" + name + "上线");
                        break;
                    }
                    case "GAMESTART": {
                        applicationModel.getTip().setText("已连接服务器,停止学生注册");
                        applicationModel.getProcess().setText("游戏开始,请老师出题");
                        break;
                    }
                    case "GAMESTOP": {
                        String tip = tokens.nextToken();
                        applicationModel.getTip().setText("已连接服务器");
                        applicationModel.getTip().setFill(Color.BLACK);
                        applicationModel.getProcess().setText(applicationModel.getProcess().getText() + "\n" + tip);
                        break;
                    }
                    case "TIP": {
                        String tip = tokens.nextToken();
                        applicationModel.getTip().setText(tip);
                        break;
                    }
                    case "SETQUESTION": {
                        applicationModel.getTip().setText("请老师出题");
                        applicationModel.getTip().setFill(Color.RED);
                        applicationModel.getSubmit().setDisable(false);
                        applicationModel.setIsQuestion(true);
                        applicationModel.getQuestionTextField().setEditable(true);
                        applicationModel.getAnswerTextField().setEditable(false);
                        break;
                    }
                    case "QUESTION": {
                        String question = tokens.nextToken();
                        applicationModel.getQuestionTextField().setText(question);
                        applicationModel.getSubmit().setDisable(false);
                        break;
                    }
                    case "TIMER": {
                        String time = tokens.nextToken();
                        applicationModel.getTip().setText("请答题,剩余时间" + time);
                        applicationModel.getTip().setFill(Color.RED);
                        break;
                    }
                    case "TIMERISOUT": {
                        applicationModel.getSubmit().setDisable(true);
                        applicationModel.getTip().setText("您已超时");
                        applicationModel.getTip().setFill(Color.RED);
                        applicationModel.getAnswerTextField().setText("");
                        applicationModel.getQuestionTextField().setText("");
                        break;
                    }
                    case "BROADCAST": {
                        String content = tokens.nextToken();
                        applicationModel.getProcess().setText(applicationModel.getProcess().getText() + "\n" + content);
                        break;
                    }

                    default: {
                        System.out.println("客户端接受消息格式错误");
                        break;
                    }
                }
            } catch (IOException e) {
                applicationModel.getTip().setText("服务器连接断开");
                applicationModel.getTip().setFill(Color.BLACK);
                try {
                    mySocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return;
            }
        }
    }
}
