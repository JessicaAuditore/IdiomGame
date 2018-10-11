package org.soul.runnable;

import org.soul.common.ThreadAdapter;

public class TimerThread implements Runnable {

    public static int time = 60;
    public static boolean gameIsStart = false;

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

                if (time == 0 && gameIsStart) {
                    time = 60;
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
