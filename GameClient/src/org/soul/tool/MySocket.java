package org.soul.tool;

import java.io.*;
import java.net.Socket;

public class MySocket {

    private Socket socket;
    private Writer writer;
    private BufferedReader bufferedReader;

    public void setSocket(Socket socket) throws IOException {
        this.socket = socket;
        writer = new OutputStreamWriter(socket.getOutputStream());
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void close() throws IOException {
        bufferedReader.close();
        writer.close();
        socket.close();
    }

    public Writer getWriter(){
        return writer;
    }

    public BufferedReader getBufferedReader(){
        return bufferedReader;
    }
}
