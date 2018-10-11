package org.soul.runnable;

import org.soul.entity.Teacher;
import org.soul.tool.MySocket;
import org.soul.tool.StudentList;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;

public class ServerThread implements Runnable {

    public static Teacher teacher;
    public static StudentList studentList;
    public static Map<String, ClientThread> clientThreads;

    private ServerSocket serverSocket;

    public ServerThread(ServerSocket server) {
        this.serverSocket = server;
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (true) {
            if (!serverSocket.isClosed()) {
                try {
                    MySocket mySocket = new MySocket();
                    mySocket.setSocket(serverSocket.accept());
                    ClientThread clientThread = new ClientThread(mySocket);
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
