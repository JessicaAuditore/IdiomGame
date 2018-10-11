package org.soul.entity;

public class Teacher extends Student {

    public Teacher(String name) {
        super("000", name);
    }

    @Override
    public int act() {
        return 0;
    }
}
