package org.soul.runnable;

import org.soul.common.ThreadAdapter;

import java.util.concurrent.ConcurrentHashMap;

public class TimerThread implements Runnable {

    public static int time = 60;
    public static boolean gameIsStart = false;
    public static boolean answerIsStart = false;

    public TimerThread() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (time > 0 && !gameIsStart) {
                    ThreadAdapter.broadcast("TIP@已连接服务器,注册剩余时间" + time);
                }
                if (time == 0 && !gameIsStart) {
                    if (!ThreadAdapter.checkActor()) {
                        gameIsStart = true;
                        return;
                    }
                    ThreadAdapter.broadcast("GAMESTART@");
                    gameIsStart = true;
                    ThreadAdapter.setQuestion();
                }

                if (time != 0 && answerIsStart) {
                    //正在答题的同学
                    String name = ServerThread.studentList.get(ThreadAdapter.i - 1).getName();
                    for (ConcurrentHashMap.Entry<String, ClientThread> entry : ServerThread.clientThreads.entrySet()) {
                        if (entry.getKey().equals(name)) {
                            ClientThread clientThread = entry.getValue();
                            clientThread.getMySocket().getWriter().write("TIMER@" + time + "eof\n");
                            clientThread.getMySocket().getWriter().flush();
                        }
                    }
                }

                if (time == 0 && answerIsStart) {
                    answerIsStart = false;
                    //回答超时
                    ThreadAdapter.broadcast("GAMESTOP@" + ServerThread.studentList.get(ThreadAdapter.i - 1).getName() + "回答超时,被淘汰,正确答案是" + ClientThread.answers.get(ThreadAdapter.i - 1).getValue() + ",下一轮游戏继续进行");

                    String name = ServerThread.studentList.get(ThreadAdapter.i - 1).getName();
                    for (ConcurrentHashMap.Entry<String, ClientThread> entry : ServerThread.clientThreads.entrySet()) {
                        if (entry.getKey().equals(name)) {
                            ClientThread clientThread = entry.getValue();
                            clientThread.getMySocket().getWriter().write("TIMERISOUT@eof\n");
                            clientThread.getMySocket().getWriter().flush();
                            //重置问题和轮转标志
                            ServerThread.studentList.remove(ServerThread.studentList.get(ThreadAdapter.i - 1));
                            ClientThread.question = ClientThread.answers.get(ThreadAdapter.i - 1);
                            ClientThread.answers = clientThread.getIdiomDao().selectSomeById(ClientThread.question.getId() + 1, ClientThread.question.getId() + ServerThread.studentList.size());
                            ThreadAdapter.i = 0;
                        }
                    }

                    if (ServerThread.studentList.size() == 1) {
                        ThreadAdapter.broadcast("GAMESTOP@游戏结束,赢者是" + ServerThread.studentList.get(0).getName());
                    } else {
                        ThreadAdapter.sendQuestion(ClientThread.question.getValue());
                    }
                }

                if (time == 0 && gameIsStart) {
                    time = 30;
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
