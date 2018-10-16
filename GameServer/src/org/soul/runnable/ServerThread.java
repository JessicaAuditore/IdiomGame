package org.soul.runnable;

import org.soul.entity.Teacher;
import org.soul.tool.MySocket;
import org.soul.tool.StudentList;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerThread implements Runnable {

    private final int PORT = 8001;

    public static Teacher teacher;
    public static StudentList studentList;
    public static Map<String, ClientThread> clientThreads;

    private ServerSocket serverSocket;

    public ServerThread() {
        try {
            this.serverSocket = new ServerSocket(PORT);
            System.out.println("服务器启动成功,请学生在" + TimerThread.time + "s时间内完成注册,等待游戏开始");
        } catch (BindException e) {
            System.out.println("服务器端口异常" + e.getMessage());
        } catch (Exception e) {
            System.out.println("服务器启动失败" + e.getMessage());
        }
        new TimerThread();
        studentList = new StudentList();
        clientThreads = new ConcurrentHashMap<>();
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (true) {
            if (!serverSocket.isClosed()) {
                try {
                    MySocket socket = new MySocket();
                    socket.setSocket(serverSocket.accept());
                    ClientThread clientThread = new ClientThread(socket);
                    String clientNickName = clientThread.getClientNickName();
                    if (clientNickName != null) {
                        clientThreads.put(clientNickName, clientThread);
                    }
                } catch (IOException e) {
                    System.out.println("建立客户端线程失败" + e.getMessage());
                }
            } else {
                System.out.println("服务器socket已关闭");
            }
        }
    }
}
