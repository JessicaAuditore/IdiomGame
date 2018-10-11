package org.soul.common;

import org.soul.tool.MySocket;

import java.io.IOException;
import java.io.Writer;

public class ThreadAdapter {

    public static void register(MySocket mySocket, String id, String name) throws IOException {
        Writer writer = mySocket.getWriter();
        writer.write("REGISTER@id:" + id + "/name:" + name + "eof\n");
        writer.flush();
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

    public static void sendMessage(MySocket mySocket, String answer) throws IOException {
        Writer writer = mySocket.getWriter();
        writer.write("ANSWER@" + answer + "eof\n");
        writer.flush();
    }
}
