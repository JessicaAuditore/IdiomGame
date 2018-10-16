package org.soul.entity;

public class Teacher extends Student {

    public Teacher(String name) {
        super("000", name);
    }

    @Override
    public String login() {
        return name + "老师上线";
    }

    @Override
    public String correct() {
        return name + "老师出题为:";
    }

    @Override
    public String error() {
        return name + "老师出题错误，游戏结束";
    }
}
