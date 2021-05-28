package ir.sample.app.PishSabteNam.models;

import java.util.ArrayList;

public class Course {

    public String id = "";
    public String name = "";
    public Department department;
    public ArrayList<Student> students;

    public Course(String id, String name, Department department) {
        this.id = id;
        this.name = name;
        this.department = department;
    }
}
