package ir.sample.app.PishSabteNam.models;

import java.util.ArrayList;

public class Student {
    public String id = "";
    public String firstName = "";
    public String lastName = "";
    public String tedadVahed;
    public ArrayList<Course> courses;

    public static int maxTedadVahed = 20;

    public Student() {
        courses = new ArrayList<>();
    }

    public void setTedadVahed(String tedadVahed) {
        this.tedadVahed = tedadVahed;
    }

    public static int calcTedadVahed(ArrayList<Course> courses) {
        int res = 0;
        for (Course c : courses) {
            res += Integer.parseInt(c.tedadVahed);
        }
        return res;
    }

    @Override
    public String toString() {
        return "Student instance: " + this.id;
    }
}
