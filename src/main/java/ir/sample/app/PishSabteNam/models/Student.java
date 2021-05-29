package ir.sample.app.PishSabteNam.models;

import java.util.ArrayList;

public class Student {
    public String id = "";
    public String firstName = "";
    public String lastName = "";
    public int tedadVahed;
    public ArrayList<Course> courses;

    public Student() {
        courses = new ArrayList<>();
    }

    public void setTedadVahed(int tedadVahed) {
        this.tedadVahed = tedadVahed;
    }

    public static int calcTedadVahed(ArrayList<Course> courses) {
        int res = 0;
        for (Course c : courses) {
            res += c.tedadVahed;
        }
        return res;
    }
}
