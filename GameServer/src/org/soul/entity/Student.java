package org.soul.entity;

public class Student extends Actor {

    protected String id;
    protected String name;

    public Student(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Student() {
        this.id = null;
        this.name = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String login() {
        return name + "同学上线";
    }

    @Override
    public String correct() {
        return name + "同学回答正确:";
    }

    @Override
    public String overtime() {
        return name + "同学回答超时,被淘汰";
    }

    @Override
    public String error() {
        return name + "同学回答错误,被淘汰";
    }

    @Override
    public String win() {
        return "游戏结束,赢者是" + name + "同学";
    }
}
