package org.soul.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class DBOperation {

    private static DBOperation instance = new DBOperation();

    List<Connection> connectionPool = null;

    public static DBOperation getInstance() {
        return instance;
    }

    private String url = "jdbc:mysql:///uml?serverTimezone=GMT&useUnicode=true&characterEncoding=UTF8&useSSL=false";
    private String username = "root";
    private String password = "xiaokaixian";
    private int maxConnection = 60;
    private int count = 0;

    private DBOperation() {
        connectionPool = new LinkedList<>();
        larger(5);
    }

    private void larger(int num) {
        for (int i = 0; i < (num > maxConnection ? maxConnection : num); i++) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager
                        .getConnection(url, username, password);
                connectionPool.add(conn);
                count++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void freeConnection(Connection connection) {
        connectionPool.add(connection);
    }

    private Connection getIdleConnection() {
        if (connectionPool.size() == 0)
            larger(5);
        return connectionPool.remove(0);
    }

    public ResultSet query(String sql) throws Exception {
        Connection connection = getIdleConnection();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        freeConnection(connection);
        return rs;
    }

    public void update(String sql) throws Exception {
        Connection connection = getIdleConnection();
        Statement stmt = connection.createStatement();
        stmt.execute(sql);
        freeConnection(connection);
    }
}
