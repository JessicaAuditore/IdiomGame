package org.soul.common;

import org.soul.dao.IdiomDao;
import org.soul.entity.Student;
import org.soul.runnable.ClientThread;
import org.soul.runnable.ServerThread;
import org.soul.runnable.TimerThread;
import org.soul.tool.MySocket;
import org.soul.tool.Tokenizer;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadAdapter {

    //广播
    public static void broadcast(String message) throws IOException {
        if (ServerThread.clientThreads != null) {
            for (ConcurrentHashMap.Entry<String, ClientThread> entry : ServerThread.clientThreads.entrySet()) {
                ClientThread clientThread = entry.getValue();
                clientThread.getSocket().getWriter().write("BROADCAST@" + message + "eof\n");
                clientThread.getSocket().getWriter().flush();
            }
        }
    }

    public static String getMessage(MySocket mySocket) throws IOException {
        StringBuffer message = new StringBuffer();
        String temp;
        int index;
        while ((temp = mySocket.getBufferedReader().readLine()) != null) {
            if ((index = temp.indexOf("eof")) != -1) {
                message.append(temp.substring(0, index));
                break;
            }
            message.append(temp);
        }
        return new String(message);
    }

    public static Student convertContentToStudent(String content) {
        Tokenizer tokens = new Tokenizer(content, "/");
        String id = tokens.nextToken().substring(3);
        String name = tokens.nextToken().substring(5);
        return new Student(id, name);
    }

    public static boolean registerStudent(MySocket mySocket, Student student) throws IOException {
        Writer writer = mySocket.getWriter();
        if (TimerThread.gameIsStart) {
            writer.write("REGISTERFAIL@注册失败,注册已超时eof\n");
            writer.flush();
            return false;
        } else if (ServerThread.studentList.add(student)) {
            writer.write("REGISTERSUCCESS@注册成功,当前在线游戏人数为" + ServerThread.studentList.size() + ",\n");
            if (ServerThread.teacher != null) {
                writer.write("老师在线,\n");
            } else {
                writer.write("老师未上线,\n");
            }
            writer.write("准备开始游戏eof\n");
            writer.flush();
            System.out.println(ServerThread.studentList.list());
            return true;
        } else {
            writer.write("REGISTERFAIL@注册失败,已有该学生注册,请重新注册eof\n");
            writer.flush();
            return false;
        }
    }

    //游戏开始前，检测游戏人员
    public static boolean checkActors() throws IOException {
        if (ServerThread.teacher == null) {
            broadcast("老师未上线，无法开始游戏");
            return false;
        } else if (ServerThread.studentList.size() <= 1) {
            broadcast("学生人数不足，无法开始游戏");
            return false;
        } else {
            return true;
        }
    }

    public static void setQuestion() throws IOException {
        for (ConcurrentHashMap.Entry<String, ClientThread> entry : ServerThread.clientThreads.entrySet()) {
            if (entry.getKey().equals(ServerThread.teacher.getName())) {
                ClientThread clientThread = entry.getValue();
                clientThread.getSocket().getWriter().write("SETQUESTION@eof\n");
                clientThread.getSocket().getWriter().flush();
            }
        }
    }

    public static void sendQuestion(String question) throws IOException {
        int number = getNumber();
        for (ConcurrentHashMap.Entry<String, ClientThread> entry : ServerThread.clientThreads.entrySet()) {
            if (entry.getKey().equals(ServerThread.studentList.get(number).getName())) {
                ClientThread clientThread = entry.getValue();
                clientThread.getSocket().getWriter().write("QUESTION@" + question + "eof\n");
                clientThread.getSocket().getWriter().flush();
            }
        }
        broadcast("请" + ServerThread.studentList.get(number).getName() + "同学在60s内答题");
        //学生回答开始计时
        TimerThread.answerIsStart = true;
        TimerThread.time = 60;
    }

    public static boolean updateAnswers(IdiomDao idiomDao) throws Exception {
        ClientThread.answers = idiomDao.selectSomeById(ClientThread.question.getId() + 1, ClientThread.question.getId() + ServerThread.studentList.size());
        if (ClientThread.answers.size() == ServerThread.studentList.size()) {
            return true;
        } else {
            return false;
        }
    }


    //轮转标志i
    public static int i = 0;

    private static int getNumber() {
        if (i >= ServerThread.studentList.size()) {
            i = 0;
        }
        return i++;
    }
}