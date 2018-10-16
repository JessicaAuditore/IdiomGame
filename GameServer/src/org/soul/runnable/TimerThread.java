package org.soul.runnable;

import org.soul.common.ThreadAdapter;
import org.soul.entity.Student;

import java.util.concurrent.ConcurrentHashMap;

public class TimerThread implements Runnable {

    public static int time = 60;
    public static boolean gameIsStart = false;
    public static boolean answerIsStart = false;
    public static boolean gameIsStop = false;

    public TimerThread() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                //学生注册以后，游戏未开始时的其他学生的注册倒计时
                if (time > 0 && !gameIsStart) {
                    ThreadAdapter.broadcast("TIP#已连接服务器,注册剩余时间" + time);
                }

                //游戏开始
                if (time == 0 && !gameIsStart) {
                    gameIsStart = true;
                    if (!ThreadAdapter.checkActors()) {
                        gameIsStop = true;
                        return;
                    }
                    ThreadAdapter.broadcast("GAMESTART#成语接龙游戏开始，本次游戏人数为" + ServerThread.studentList.size() + ",请" + ServerThread.teacher.getName() + "老师出题");
                    ThreadAdapter.setQuestion();
                }

                //学生回答倒计时
                if (time != 0 && answerIsStart && !gameIsStop) {
                    String name = ServerThread.studentList.get(ThreadAdapter.i - 1).getName();
                    for (ConcurrentHashMap.Entry<String, ClientThread> entry : ServerThread.clientThreads.entrySet()) {
                        if (entry.getKey().equals(name)) {
                            ClientThread clientThread = entry.getValue();
                            clientThread.getSocket().getWriter().write("TIMER@" + time + "eof\n");
                            clientThread.getSocket().getWriter().flush();
                        }
                    }
                }

                //学生回答超时
                if (time == 0 && answerIsStart && !gameIsStop) {
                    answerIsStart = false;
                    int number = ThreadAdapter.i - 1;
                    Student student = ServerThread.studentList.get(number);
                    ThreadAdapter.broadcast(student.overtime() + "正确答案是" + ClientThread.answers.get(number).getValue());

                    String name = student.getName();
                    //寻找正在答题的学生绑定的客户端线程
                    for (ConcurrentHashMap.Entry<String, ClientThread> entry : ServerThread.clientThreads.entrySet()) {
                        if (entry.getKey().equals(name)) {
                            ClientThread clientThread = entry.getValue();
                            clientThread.getSocket().getWriter().write("TIMERISOUT@eof\n");
                            clientThread.getSocket().getWriter().flush();
                            //重置问题和轮转标志
                            ServerThread.studentList.remove(student);
                            ClientThread.question = ClientThread.answers.get(number);
                            if (!ThreadAdapter.updateAnswers(clientThread.getIdiomDao())) {
                                //游戏结束
                                ThreadAdapter.broadcast("题库不足,游戏结束");
                                gameIsStop = true;
                            }
                            ThreadAdapter.i = 0;
                        }
                    }

                    if (ServerThread.studentList.size() == 1) {
                        gameIsStop = true;
                        ThreadAdapter.broadcast(ServerThread.studentList.get(0).win());
                    } else {
                        ThreadAdapter.broadcast("下一轮游戏继续进行");
                        ThreadAdapter.sendQuestion(ClientThread.question.getValue());
                    }
                }

                //游戏过程中
                if (time == 0 && gameIsStart && !gameIsStop) {
                    time = 30;
                }

                //游戏结束
                if (gameIsStart && gameIsStop) {
                    return;
                }

                time--;
                System.out.println(time);
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
