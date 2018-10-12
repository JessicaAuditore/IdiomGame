package org.soul.runnable;

import org.soul.common.ThreadAdapter;
import org.soul.dao.IdiomDao;
import org.soul.dao.impl.IdiomDaoImpl;
import org.soul.entity.Idiom;
import org.soul.entity.Student;
import org.soul.tool.MySocket;
import org.soul.tool.Tokenizer;

import java.io.IOException;
import java.util.List;

public class ClientThread implements Runnable {

    public static Idiom question;
    public static List<Idiom> answers;

    private IdiomDao idiomDao;
    private MySocket mySocket;
    private String clientNickName;
    private boolean isRunning;

    public ClientThread(MySocket mySocket) {
        this.mySocket = mySocket;
        this.idiomDao = new IdiomDaoImpl();
        isRunning = initialize();
        new Thread(this).start();
    }

    private synchronized boolean initialize() {
        try {
            String message = ThreadAdapter.getMessage(mySocket);
            Tokenizer tokens = new Tokenizer(message, "@");
            String messageType = tokens.nextToken();
            if ("REGISTER".equals(messageType)) {
                String content = tokens.nextToken();
                Student student = ThreadAdapter.convertContentToStudent(content);
                if (ThreadAdapter.registerStudent(mySocket, student)) {
                    this.clientNickName = student.getName();
                    if (student.getId().equals("000")) {
                        ThreadAdapter.broadcast("LOGIN@老师" + clientNickName);
                    } else {
                        ThreadAdapter.broadcast("LOGIN@学生" + clientNickName);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("initialize无法读取下一行" + e.getMessage());
        }
        return true;
    }

    public MySocket getMySocket() {
        return mySocket;
    }

    public String getClientNickName() {
        return clientNickName;
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                String message = ThreadAdapter.getMessage(mySocket);
                Tokenizer tokens = new Tokenizer(message, "@");
                String messageType = tokens.nextToken();
                switch (messageType) {
                    case "ANSWER": {
                        String answer = tokens.nextToken();
                        if (question == null) {
                            //老师出题
                            question = idiomDao.selectOneByValue(answer);
                            if (question == null) {
                                //出题错误
                                ThreadAdapter.broadcast("GAMESTOP@老师出题错误，游戏结束");
                                return;
                            }
                            //出题正确
                            ThreadAdapter.broadcast("BROADCAST@老师出题为" + question.getValue());
                            answers = idiomDao.selectSomeById(question.getId() + 1, question.getId() + ServerThread.studentList.size());
                        } else {
                            if (!answer.equals(answers.get(ThreadAdapter.i - 1).getValue())) {
                                //回答错误
                                ThreadAdapter.broadcast("GAMESTOP@" + ServerThread.studentList.get(ThreadAdapter.i - 1).getName() + "回答错误,被淘汰,正确答案是" + answers.get(ThreadAdapter.i - 1).getValue() + ",第二轮游戏继续进行");
                                ServerThread.studentList.remove(ServerThread.studentList.get(ThreadAdapter.i - 1));
                                //重置问题和轮转标志
                                question = answers.get(ThreadAdapter.i - 1);
                                answers = idiomDao.selectSomeById(question.getId() + 1, question.getId() + ServerThread.studentList.size());
                                ThreadAdapter.i = 0;
                                if (ServerThread.studentList.size() == 1) {
                                    ThreadAdapter.broadcast("GAMESTOP@游戏结束,赢者是" + ServerThread.studentList.get(0).getName());
                                    return;
                                }
                            } else {
                                //回答正确
                                ThreadAdapter.broadcast("GAMESTOP@" + ServerThread.studentList.get(ThreadAdapter.i - 1).getName() + "同学回答正确:" + answer);
                                question = idiomDao.selectOneByValue(answer);
                                if (ThreadAdapter.i == ServerThread.studentList.size()) {
                                    answers = idiomDao.selectSomeById(question.getId() + 1, question.getId() + ServerThread.studentList.size());
                                }
                            }
                        }
                        ThreadAdapter.sendQuestion(question.getValue());
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public IdiomDao getIdiomDao() {
        return idiomDao;
    }
}
