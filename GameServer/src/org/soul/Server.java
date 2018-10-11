package org.soul;

import org.soul.runnable.ServerThread;
import org.soul.runnable.TimerThread;
import org.soul.tool.StudentList;

import java.net.BindException;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private void start() {
        int port = 8001;
        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("服务器启动成功");
            new TimerThread();
            ServerThread.studentList = new StudentList();
            ServerThread.clientThreads = new ConcurrentHashMap<>();
            new ServerThread(server);
        } catch (BindException e) {
            System.out.println("服务器端口异常" + e.getMessage());
        } catch (Exception e) {
            System.out.println("服务器启动失败" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Server().start();
    }
}
