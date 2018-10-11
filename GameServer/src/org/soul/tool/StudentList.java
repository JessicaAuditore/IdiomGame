package org.soul.tool;

import org.soul.entity.Student;
import org.soul.entity.Teacher;
import org.soul.runnable.ServerThread;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StudentList extends ArrayList<Student> {

    private List<Student> studentList;

    public StudentList() {
        this.studentList = new ArrayList<>();
    }

    public List<Student> list() {
        return studentList;
    }

    @Override
    public int size() {
        return studentList.size();
    }

    @Override
    public boolean add(Student student) {
        if (isNotExist(student)) {
            if (student.getId().equals("000")) {
                ServerThread.teacher = new Teacher(student.getName());
            } else {
                studentList.add(student);
                //排序
                studentList.sort(new SortById());
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Student get(int index) {
        return studentList.get(index);
    }

    @Override
    public boolean remove(Object o) {
        return studentList.remove(o);
    }

    @Override
    public String toString() {
        return studentList.size() + "";
    }

    private boolean isNotExist(Student student) {
        for (Student s : studentList) {
            if (s.getId().equals(student.getId())) {
                return false;
            }
        }
        return true;
    }

    class SortById implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            Student idiom1 = (Student) o1;
            Student idiom2 = (Student) o2;
            if (Integer.parseInt(idiom1.getId()) > Integer.parseInt(idiom2.getId())) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
