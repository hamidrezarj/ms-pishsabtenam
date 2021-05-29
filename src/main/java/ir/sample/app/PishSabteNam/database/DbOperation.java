package ir.sample.app.PishSabteNam.database;

import ir.sample.app.PishSabteNam.models.Course;
import ir.sample.app.PishSabteNam.models.Department;
import ir.sample.app.PishSabteNam.models.Pelak;
import ir.sample.app.PishSabteNam.models.Student;
//import jdk.internal.vm.compiler.collections.EconomicMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

public class DbOperation {

    public static void registerPelak(Pelak pelak, String number, Connection connection) {
        try {
            String count = "SELECT COUNT(*) FROM numberpelak";
            PreparedStatement pcount = connection.prepareStatement(count);
            ResultSet rcount = pcount.executeQuery();
            int countnum = 0;
            while (rcount.next()) {
                countnum = Integer.parseInt(rcount.getString(1));
            }
            String checkSql = "INSERT INTO numberpelak(number,first,second,third,type,harf,name,bedehi,id,show) VALUES (?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, number);
            pstmt.setString(2, pelak.first);
            pstmt.setString(3, pelak.second);
            pstmt.setString(4, pelak.third);
            pstmt.setString(5, pelak.type);
            pstmt.setString(6, pelak.harf);
            pstmt.setString(7, pelak.name);
            pstmt.setString(8, String.valueOf(pelak.bedehi));
            pstmt.setString(9, "idpelak" + String.valueOf(countnum));
            pstmt.setString(10, String.valueOf(true));
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Student retrieveStudent(String user_id, Connection connection) {
        try {
            String query = "SELECT shomaredaneshjooyi FROM student WHERE shomaredaneshjooyi=?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, user_id);
            ResultSet resultSet = pstmt.executeQuery();
            Student student = new Student();
            while (resultSet.next()) {

                student.id = resultSet.getString(1);
            }

            query = "SELECT shomaredaneshjooyi, courseid FROM takencourses WHERE shomaredaneshjooyi=?";

            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, user_id);
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                String courseid = resultSet.getString(2);
//                student.id = resultSet.getString(1);
                Course course = DbOperation.retrieveCourse(courseid, connection);
                student.courses.add(course);
                // Retrieve Student's tedadvahed
            }
            return student;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Course> retrieveCourses(String dep, Connection connection) {
        try {
            String query = "SELECT courseid, coursename, coursedep, courseprofessor, firstday, secondday, maximumcapacity, availablecapacity, tedadvahed FROM courses WHERE coursedep=?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, dep);
            ResultSet resultSet = pstmt.executeQuery();
            String data[] = new String[10];
            ArrayList<Course> courses = new ArrayList<>();
            while (resultSet.next()) {

                for (int i = 1; i <= 9; i++) {
                    data[i] = resultSet.getString(i).trim();
                }
                Department department = new Department();
                department.name = data[3];

                Course course = new Course(data[1], data[2], department);
                course.teacher = data[4];
                course.firstPresentDate = data[5];
                course.secondPresentDate = data[6];
                course.capacity = Integer.parseInt(data[7]);
                course.reservedCnt = Integer.parseInt(data[8]);
                course.tedadVahed = Integer.parseInt(data[9]);

                courses.add(course);
            }
            return courses;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public static Course retrieveCourse(String courseID, Connection connection) {
        try {
            String query = "SELECT courseid, coursename, coursedep, courseprofessor, firstday, secondday, maximumcapacity, availablecapacity, tedadvahed FROM courses WHERE courseid=?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, courseID);
            ResultSet resultSet = pstmt.executeQuery();
            String data[] = new String[10];
            Course course = new Course();
            ArrayList<Course> courses = new ArrayList<>();

            while (resultSet.next()) {
                for (int i = 1; i <= 9; i++) {
                    data[i] = resultSet.getString(i).trim();
                }
                course.id = data[1];
                course.name = data[2];

                Department department = new Department();
                department.name = data[3];
                course.department = department;

                course.teacher = data[4];
                course.firstPresentDate = data[5];
                course.secondPresentDate = data[6];
                course.capacity = Integer.parseInt(data[7]);
                course.reservedCnt = Integer.parseInt(data[8]);
                course.tedadVahed = Integer.parseInt(data[9]);

                courses.add(course);

            }
            return courses.get(0);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }


    public static ArrayList<Department> retrieveDepList(Connection connection) {

        try {
            String query = "SELECT courses.courseid, courses.coursename, courses.coursedep FROM courses";
            PreparedStatement pstmt = null;
            pstmt = connection.prepareStatement(query);
            ResultSet resultSet = pstmt.executeQuery();
            String data[] = new String[4];
            ArrayList<Department> departments = new ArrayList<>();
//            HashSet<Department> departments = new HashSet<>();
//            HashSet<String> departments = new HashSet<>();
            while (resultSet.next()) {
                Department department = new Department();
                for (int i = 1; i <= 3; i++) {
                    data[i] = resultSet.getString(i).trim();
                }
                department.name = data[3];
                if (!departments.contains(department)) {
                    departments.add(department);
                }
            }
//            Department[] res = new Department[departments.size()];
            return departments;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }

    }

    public static ArrayList<Pelak> retrievePelaks(String number, Connection connection) {
        try {
            String checkSql = "SELECT first,second,third,type,harf,name,bedehi,id,show FROM numberpelak where number=? and show=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, number);
            pstmt.setString(2, String.valueOf(true));
            ResultSet resultSet = pstmt.executeQuery();
            String data[] = new String[10];
            ArrayList<Pelak> pelaks = new ArrayList<>();
            while (resultSet.next()) {
                Pelak pelak = new Pelak();
                for (int i = 1; i <= 9; i++) {
                    data[i] = resultSet.getString(i);
                }
                pelak.first = data[1];
                pelak.second = data[2];
                pelak.third = data[3];
                pelak.type = data[4];
                pelak.harf = data[5];
                pelak.name = data[6];
                pelak.bedehi = Integer.parseInt(data[7]);
                pelak.id = data[8];
                if (Boolean.valueOf(data[9])) {
                    pelaks.add(pelak);
                }
            }
            return pelaks;
        } catch (Exception e) {
            return null;
        }
    }

    public static Pelak retrievePelak(String pelakid, Connection connection) {
        try {
            String checkSql = "SELECT first,second,third,type,harf,name,bedehi,id,show FROM numberpelak where id=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, pelakid);
            ResultSet resultSet = pstmt.executeQuery();
            String data[] = new String[10];
            ArrayList<Pelak> pelaks = new ArrayList<>();
            while (resultSet.next()) {
                Pelak pelak = new Pelak();
                for (int i = 1; i <= 9; i++) {
                    data[i] = resultSet.getString(i);
                }
                pelak.first = data[1];
                pelak.second = data[2];
                pelak.third = data[3];
                pelak.type = data[4];
                pelak.harf = data[5];
                pelak.name = data[6];
                pelak.bedehi = Integer.parseInt(data[7]);
                pelak.id = data[8];
                if (Boolean.valueOf(data[9])) {
                    pelaks.add(pelak);
                }
            }
            return pelaks.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void deletepelak(String pelakid, Connection connection) {
        try {
            String checkSql = "UPDATE numberpelak set show=? where id=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, String.valueOf(false));
            pstmt.setString(2, pelakid);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void editpelak(Pelak pelak, Connection connection) {
        try {
            String checkSql = "UPDATE numberpelak set first=?,second=?,third=?,type=?,harf=?,name=? where id=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, pelak.first);
            pstmt.setString(2, pelak.second);
            pstmt.setString(3, pelak.third);
            pstmt.setString(4, pelak.type);
            pstmt.setString(5, pelak.harf);
            pstmt.setString(6, pelak.name);
            pstmt.setString(7, pelak.id);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void delete() {
        String d = "";
        try {
            Connection connection = DatabaseManager.getConnection();
            String checkSql = "DELETE FROM numberpelak;";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.executeQuery();
            pstmt.close();
            connection.close();
        } catch (Exception e) {
            System.out.println("deleted");
        }
    }
}
