package org.soul.runnable;

import javafx.scene.paint.Color;
import org.soul.common.ThreadAdapter;
import org.soul.model.GameApplicationModel;
import org.soul.tool.MySocket;
import org.soul.tool.Tokenizer;

import java.io.IOException;

public class ClientThread implements Runnable {

    private MySocket socket;
    private GameApplicationModel applicationModel;

    public ClientThread(MySocket socket, GameApplicationModel applicationModel) {
        this.socket = socket;
        this.applicationModel = applicationModel;
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                String message = ThreadAdapter.getMessage(socket);
                Tokenizer tokens = new Tokenizer(message, "@");
                String messageType = tokens.nextToken();
                switch (messageType) {
                    case "REGISTERSUCCESS": {
                        String tip = tokens.nextToken();
                        applicationModel.addProcess(tip);
                        applicationModel.getRegister().setDisable(true);
                        break;
                    }
                    case "REGISTERFAIL": {
                        String tip = tokens.nextToken();
                        applicationModel.addProcess(tip);
                        applicationModel.getTip().setText("已连接服务器");
                        applicationModel.getTip().setFill(Color.BLACK);
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
                        Tokenizer tokenizer = new Tokenizer(content, "#");
                        if (tokenizer.size() != 1) {
                            switch (tokenizer.nextToken()) {
                                case "TIP": {
                                    String tip = tokenizer.nextToken();
                                    applicationModel.getTip().setText(tip);
                                    break;
                                }
                                case "GAMESTART": {
                                    String tip = tokenizer.nextToken();
                                    applicationModel.getTip().setText("已连接服务器,停止学生注册");
                                    applicationModel.setProcess(tip);
                                    break;
                                }
                            }
                        } else {
                            applicationModel.addProcess("广播消息：" + content);
                            applicationModel.getTip().setText("已连接服务器");
                            applicationModel.getTip().setFill(Color.BLACK);
                        }
                        break;
                    }
                    default: {
                        System.out.println("客户端接受消息格式错误");
                        break;
                    }
                }
            } catch (IOException e) {
                applicationModel.getTip().setText("服务器连接断开");
                applicationModel.getTip().setFill(Color.RED);
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return;
            }
        }
    }
}
