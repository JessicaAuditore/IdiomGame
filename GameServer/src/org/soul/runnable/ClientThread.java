package org.soul.runnable;

import org.soul.common.ThreadAdapter;
import org.soul.dao.IdiomDao;
import org.soul.dao.impl.IdiomDaoImpl;
import org.soul.entity.Idiom;
import org.soul.entity.Student;
import org.soul.entity.Teacher;
import org.soul.tool.MySocket;
import org.soul.tool.Tokenizer;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;

public class ClientThread implements Runnable {

    public static Idiom question;
    public static List<Idiom> answers;

    private IdiomDao idiomDao;
    private MySocket socket;
    private String clientNickName;
    private boolean isRunning;

    public ClientThread(MySocket socket) {
        this.socket = socket;
        this.idiomDao = new IdiomDaoImpl();
        isRunning = initialize();
        new Thread(this).start();
    }

    private synchronized boolean initialize() {
        try {
            String message = ThreadAdapter.getMessage(socket);
            Tokenizer tokens = new Tokenizer(message, "@");
            if ("REGISTER".equals(tokens.nextToken())) {
                Student student = ThreadAdapter.convertContentToStudent(tokens.nextToken());
                if (ThreadAdapter.registerStudent(socket, student)) {
                    this.clientNickName = student.getName();
                    if (student.getId().equals("000")) {
                        ThreadAdapter.broadcast((new Teacher(student.getName()).login()));
                    } else {
                        ThreadAdapter.broadcast(student.login());
                    }
                } else {
                    return false;
                }
            }
        } catch (IOException e) {
            System.out.println("initialize无法读取下一行" + e.getMessage());
        }
        return true;
    }

    public MySocket getSocket() {
        return socket;
    }

    public String getClientNickName() {
        return clientNickName;
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                String message = ThreadAdapter.getMessage(socket);
                Tokenizer tokens = new Tokenizer(message, "@");
                if ("ANSWER".equals(tokens.nextToken())) {
                    String answer = tokens.nextToken();
                    if (question == null) {
                        //正在出题的老师
                        Teacher teacher = ServerThread.teacher;
                        question = idiomDao.selectOneByValue(answer);
                        if (question == null) {
                            //老师出题错误,游戏结束
                            ThreadAdapter.broadcast(teacher.error());
                            TimerThread.gameIsStop = true;
                            return;
                        }
                        //老师出题正确
                        ThreadAdapter.broadcast(teacher.correct() + question.getValue());
                        if (!ThreadAdapter.updateAnswers(idiomDao)) {
                            //游戏结束
                            ThreadAdapter.broadcast("题库不足,游戏结束");
                            TimerThread.gameIsStop = true;
                            return;
                        }
                    } else {
                        int number = ThreadAdapter.i - 1;
                        //正在回答问题的学生
                        Student student = ServerThread.studentList.get(number);
                        if (!answer.equals(answers.get(number).getValue())) {
                            //学生回答错误
                            ThreadAdapter.broadcast(student.error() + ",正确答案是" + answers.get(number).getValue());
                            ServerThread.studentList.remove(student);
                            //判断学生剩余人数
                            if (ServerThread.studentList.size() == 1) {
                                //游戏结束
                                ThreadAdapter.broadcast(ServerThread.studentList.get(0).win());
                                TimerThread.gameIsStop = true;
                                return;
                            }
                            //重置问题,答案集和轮转标志,游戏继续进行
                            question = answers.get(number);
                            if (!ThreadAdapter.updateAnswers(idiomDao)) {
                                //游戏结束
                                ThreadAdapter.broadcast("题库不足,游戏结束");
                                TimerThread.gameIsStop = true;
                                return;
                            }
                            ThreadAdapter.i = 0;
                            ThreadAdapter.broadcast("下一轮游戏继续进行");
                        } else {
                            //学生回答正确
                            ThreadAdapter.broadcast(student.correct() + answer);
                            question = idiomDao.selectOneByValue(answer);
                            if (ThreadAdapter.i == ServerThread.studentList.size()) {
                                if (!ThreadAdapter.updateAnswers(idiomDao)) {
                                    //游戏结束
                                    ThreadAdapter.broadcast("题库不足,游戏结束");
                                    TimerThread.gameIsStop = true;
                                    return;
                                }
                            }
                        }
                    }
                    ThreadAdapter.sendQuestion(question.getValue());
                }
            } catch (SocketException e) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public IdiomDao getIdiomDao() {
        return idiomDao;
    }


}
