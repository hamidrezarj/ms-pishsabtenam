package ir.sample.app.PishSabteNam.models;

import java.util.ArrayList;

public class Course {

    public String id;
    public String name;
    public int capacity;
    public int reservedCnt;
    public int tedadVahed;
    public String teacher;
    public String firstPresentDate;
    public String secondPresentDate;
    public Department department;
    public ArrayList<Student> students;

    public Course(String id, String name, Department department) {
        this.id = id;
        this.name = name;
        this.department = department;
    }

    public Course() {
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Course)) {
            return false;
        }

        Course c = (Course) obj;

        return c.name.equals(this.name);
    }
}
