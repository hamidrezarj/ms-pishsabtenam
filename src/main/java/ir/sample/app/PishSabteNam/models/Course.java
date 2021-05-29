package ir.sample.app.PishSabteNam.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class Course {

    public String id;
    public String name;
    public String capacity;
    public String reservedCnt;
    public String tedadVahed;
    public String teacher;
    public String firstPresentDate;
    public String secondPresentDate;
    public Department department;
    public ArrayList<Student> students;

    public static String farsiNums = "۰۱۲۳۴۵۶۷۸۹";
    public static String dateDelimiter = "_";

    public Course(String id, String name, Department department) {
        this.id = id;
        this.name = name;
        this.department = department;
    }

    public Course() {
    }

    @Override
    public String toString() {
        return this.name + " " + this.id;
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

        return c.id.equals(this.id);
    }

    public static String convToFarsiDate(String day) {
        String res;

        switch (day) {
            case "0":
                res = "شنبه";
                break;
            case "1":
                res = "یکشنبه";
                break;
            case "2":
                res = "دوشنبه";
                break;
            case "3":
                res = "سه شنبه";
                break;
            case "4":
                res = "چهارشنبه";
                break;
            case "5":
                res = "پنج شنبه";
                break;
            case "6":
                res = "جمعه";
                break;
            default:
                res = "شنبه";
                break;
        }

        return res;
    }

    public static boolean checkTimeConflicts(Course c1, Course c2, String delimiter) {

        String c1_firstDay, c1_secondDay, c1_firstTime, c1_secondTime;
        String c2_firstDay, c2_secondDay, c2_firstTime, c2_secondTime;

        String[] c1_firstDate = c1.firstPresentDate.split(delimiter);
        c1_firstDay = c1_firstDate[0];
        c1_firstTime = convToEngNum(c1_firstDate[1]);

        String[] c1_secondDate = c1.secondPresentDate.split(delimiter);
        c1_secondDay = c1_secondDate[0];
        c1_secondTime = convToEngNum(c1_secondDate[1]);

        String[] c2_firstDate = c2.firstPresentDate.split(delimiter);
        c2_firstDay = c2_firstDate[0];
        c2_firstTime = convToEngNum(c2_firstDate[1]);

        String[] c2_secondDate = c2.secondPresentDate.split(delimiter);
        c2_secondDay = c2_secondDate[0];
        c2_secondTime = convToEngNum(c2_firstDate[1]);

        //First compare days
        if (!c1_firstDay.equals(c2_firstDay) && !c1_secondDay.equals(c2_firstDay) &&
                !c2_secondDay.equals(c1_firstDay) && !c2_secondDay.equals(c1_secondDay)) {
            return false;
        }

        if (c1_firstDay.equals(c2_firstDay) || c1_firstDay.equals(c2_secondDay)) {
            if (timesHaveConflicts(c1_firstTime, c2_firstTime, "-") ||
                    timesHaveConflicts(c1_firstTime, c2_secondTime, "-")) {
                return true;
            }
        }

        if (c1_secondDay.equals(c2_firstDay) || c1_secondDay.equals(c2_secondDay)) {
            if (timesHaveConflicts(c1_secondTime, c2_firstTime, "-") ||
                    timesHaveConflicts(c1_secondTime, c2_secondTime, "-")) {
                return true;
            }
        }

        return false;


    }

    public static boolean timesHaveConflicts(String t1, String t2, String splitter) {
        Date t1_start, t1_end, t2_start, t2_end;

        SimpleDateFormat parser = new SimpleDateFormat("HH:mm");

        String[] t1_split = t1.split(splitter);
        String[] t2_split = t2.split(splitter);
        try {
            t1_start = parser.parse(t1_split[0]);
            t1_end = parser.parse(t1_split[1]);
            t2_start = parser.parse(t2_split[0]);
            t2_end = parser.parse(t2_split[1]);

            if ((t1_start.equals(t2_start) || t1_start.after(t2_start)) && (t1_start.before(t2_end))) {
                return true;
            }

            if (t1_end.after(t2_start) && (t1_end.before(t2_end) || t1_end.equals(t2_end))) {
                return true;
            }

            if ((t2_start.equals(t1_start) || t2_start.after(t1_start)) && (t2_start.before(t1_end))) {
                return true;
            }

            if (t2_end.after(t1_start) && (t2_end.before(t1_end) || t2_end.equals(t1_end))) {
                return true;
            }

            return false;

        } catch (ParseException e) {
            e.printStackTrace();
            return true;
        }


    }

    public static char convCharToFarsi(char ch) {
        char res;

        switch (ch) {
            case '0':
                res = '۰';
                break;
            case '1':
                res = '۱';
                break;
            case '2':
                res = '۲';
                break;
            case '3':
                res = '۳';
                break;
            case '4':
                res = '۴';
                break;
            case '5':
                res = '۵';
                break;
            case '6':
                res = '۶';
                break;
            case '7':
                res = '۷';
                break;
            case '8':
                res = '۸';
                break;
            case '9':
                res = '۹';
                break;
            default:
                res = '۰';
                break;
        }

        if (farsiNums.indexOf(ch) != -1) {

            res = ch;
        }

        return res;
    }

    public static String convToRepresentFormat(String presentDate, String splitter) {
        StringBuilder res = new StringBuilder();

        String[] splitString = presentDate.split(splitter);

        String day = convToFarsiDate(splitString[0]);
        res.append(day);
        res.append(" ");

        // String to char array.
        String farsiTime = convToFarsiNum(splitString[1]);
        res.append(farsiTime);

        System.out.println(res.toString());

        return res.toString();
    }

    public static void convToRepUtility(ArrayList<Course> courses) {

        for (Course c : courses) {

            c.id = Course.convToFarsiNum(c.id);
            c.capacity = Course.convToFarsiNum(c.capacity);
            c.tedadVahed = Course.convToFarsiNum(c.tedadVahed);
            c.reservedCnt = Course.convToFarsiNum(c.reservedCnt);
            c.firstPresentDate = convToRepresentFormat(c.firstPresentDate, dateDelimiter);
            c.secondPresentDate = convToRepresentFormat(c.secondPresentDate, dateDelimiter);
        }

//        return courses;
    }

    public static String convToFarsiNum(String string) {
        StringBuilder res = new StringBuilder();

        for (char ch : string.toCharArray()) {
            if (Character.isDigit(ch)) {

                res.append(convCharToFarsi(ch));
            } else
                res.append(ch);
        }

        return res.toString();
    }

    public static String convToEngNum(String string) {
        StringBuilder res = new StringBuilder();

        for (char ch : string.toCharArray()) {
            if (farsiNums.indexOf(ch) != -1) {

                res.append(Integer.parseInt(Character.toString(ch)));
            } else
                res.append(ch);
        }

        return res.toString();
    }

    public static ArrayList<Course> listDifference(ArrayList<Course> all, ArrayList<Course> taken) {
        HashSet<Course> allSet = new HashSet<>(all);
        HashSet<Course> takenSet = new HashSet<>(taken);

        allSet.removeAll(takenSet);

        ArrayList<Course> res = new ArrayList<>(allSet);
        return res;

    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.name.hashCode();
        result = 31 * result + Integer.parseInt(this.capacity);
        result = 31 * result + this.id.hashCode();
        return result;
    }
}
