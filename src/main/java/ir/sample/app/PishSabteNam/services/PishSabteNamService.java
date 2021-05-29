package ir.sample.app.PishSabteNam.services;

import ir.appsan.sdk.APSService;
import ir.appsan.sdk.View;
import ir.appsan.sdk.ViewUpdate;
import ir.appsan.sdk.Response;
import ir.sample.app.PishSabteNam.database.DatabaseManager;
import ir.sample.app.PishSabteNam.database.DbOperation;
import ir.sample.app.PishSabteNam.models.*;
import ir.sample.app.PishSabteNam.views.*;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

public class PishSabteNamService extends APSService {

    String selectedharf = "";
    String selectedtype = "";
    String selectedid = "";
    Owner owner = new Owner();
    Pelak pelak = new Pelak();
    Connection connection = DatabaseManager.getConnection();
    boolean allowmake = true;

    ArrayList<Course> courseTakeAttempt = new ArrayList<>();


    public PishSabteNamService(String channelName) {
        super(channelName);
    }

    @Override
    public String getServiceName() {
        return "app:hamid_rrj:PishSabteNam";
    }

    @Override
    public View onCreateView(String command, JSONObject pageData, String userId) {
        View view;


        if (command.equals("see_curriculum")) {
            view = new SeeCurriculumView();

            CourseList courseList = new CourseList();

            // call repFormat whenever you want to represent courses in xml
            courseList.courses = DbOperation.retrieveTakenCourses(userId, connection);
            Course.convToRepUtility(courseList.courses);
            System.out.println(courseList.courses);

            view.setMustacheModel(courseList);
            return view;
        } else if (command.equals("choose_department")) {
            DepartmentList departmentList = new DepartmentList();
            departmentList.departments = DbOperation.retrieveDepList(connection);

            System.out.println(departmentList.departments);
            view = new ChooseDepartment();
            view.setMustacheModel(departmentList);
            return view;
        } else if (command.equals("add_course")) {
            return new ChooseDepartment();
        }
        else if(command.equals("choose_dep")){
            view = new ChooseDepartment();
            DepartmentList departmentList = new DepartmentList();
            departmentList.departments = DbOperation.retrieveDepList(connection);

            view.setMustacheModel(departmentList);
            return view;
        }
        else {
            System.out.println("Student ID: " + userId);
            Student student = DbOperation.retrieveStudent(userId, connection);
            // Register Student for first time into DB
            if (student.id.isEmpty()) {
//                System.out.println("NULL STUDENT");
                DbOperation.registerStudent(userId, 0, connection);
            }
            view = new HomeView();
            return view;
        }

//        if (command.equals("addcar")) {
//            return new AddCar();
//        } else if (command.startsWith("idpelak")) {
//            pelak = DbOperation.retrievePelak(command, connection);
//            view = new HazineSee();
//            view.setMustacheModel(pelak); //todo: empty mustache! ://
//            return view;
//        } else if (command.equals("pardakhts")) {
//            return new NoPardakht();
//        } else if (command.equals("rahnama")) {
//            System.out.println("1");
//            return new Rahnama();
//        } else if (command.equals("deleteandseepelaks")) {
//            DbOperation.deletepelak(selectedid, connection);
//            owner.id = userId;
//            owner.pelaks = DbOperation.retrievePelaks(userId, connection);
//            if (owner.pelaks.size() == 0) {
//                return new Enter();
//            } else {
//                view = new TarheTraffic();
//                view.setMustacheModel(owner);
//                return view;
//            }
//        } else if (command.equals("seepelaks")) {
//            owner.id = userId;
//            owner.pelaks = DbOperation.retrievePelaks(userId, connection);
//            if (owner.pelaks.size() == 0) {
//                return new Enter();
//            } else {
//                view = new TarheTraffic();
//                view.setMustacheModel(owner);
//                return view;
//            }
//        }
//        owner.id = userId;
//        owner.pelaks = DbOperation.retrievePelaks(userId, connection);
//        if (owner.pelaks.size() == 0) {
//            return new Enter();
//        } else {
//            view = new TarheTraffic();
//            view.setMustacheModel(owner);
//            return view;
//        }
    }

    @Override
    public Response onUpdate(ViewUpdate update, String updateCommand, JSONObject pageData, String userId) {

        View view;

        if (updateCommand.equals("choose_dep")) {
            System.out.println(pageData);
            view = new ChooseDepartment();
            return view;
        } else if (updateCommand.equals("see_courses")) {
            System.out.println(pageData);
            String selectedDep = pageData.get("dep_dropdown").toString();
            if (!selectedDep.equals("انتخاب دانشکده")) {
                update.addChildUpdate("dep_error", "text", "");
                view = new ChooseCurriculum();
//                view = new CourseSelectDialog();
                CourseList courseList = new CourseList();

                // call repFormat whenever you want to represent courses in xml
                // AllCourses  - Taken courses =  our goal
                ArrayList<Course> allCourses = DbOperation.retrieveCourses(selectedDep, connection);
                ArrayList<Course> takenCourses = DbOperation.retrieveTakenCourses(userId, connection);

                courseList.courses = Course.listDifference(allCourses, takenCourses);
                Course.convToRepUtility(courseList.courses);
                System.out.println(courseList.courses);


                view.setMustacheModel(courseList);
                return view;
            } else {
                //render error
                update.addChildUpdate("dep_error", "text", "نوع دانشکده نمی تواند خالی باشد.");
                return update;
            }

        } else if (updateCommand.equals("courses_chosen")) {

            // first make courseAttempt array
            for (Object obj : pageData.keySet()) {
                String arg = (String) obj;
                if (arg.length() == 10 && arg.contains("_") && pageData.get(arg).equals("true")) {
                    String courseID = arg;
                    Course selectedCourse = DbOperation.retrieveCourse(Course.convToEngNum(courseID), connection);
                    System.out.println(selectedCourse);

                    if (!courseTakeAttempt.contains(selectedCourse)) {
                        courseTakeAttempt.add(selectedCourse);
                    }

                }
            }
            boolean errorExists = false;

            // if student's maximum tedadvahed reached do sth
            Student currentStudent = DbOperation.retrieveStudent(userId, connection);
            if (Integer.parseInt(currentStudent.tedadVahed) + Student.calcTedadVahed(courseTakeAttempt)
                    > Student.maxTedadVahed) {
                //Render error for maximum vahed reached.
                String error = String.format("تعداد دروس انتخاب شده از سقف %d واحد بیشتر است.", Student.maxTedadVahed);
                update.addChildUpdate("max_tedadvahed_error", "text", error);

                errorExists = true;
            } else {

                // if already has some courses in db... do sth

                // if courses have time conflicts
                ArrayList<Course> deletedCourses = new ArrayList<>();
                for (Course c1 : courseTakeAttempt) {
                    for (Course c2 : courseTakeAttempt) {
                        if (c1.equals(c2))
                            continue;

                        if (deletedCourses.contains(c1) && deletedCourses.contains(c2))
                            continue;

                        if (Course.checkTimeConflicts(c1, c2, "_")) {
                            // Render error
                            String errorValue = String.format("دروس %s و %s با هم تداخل دارند.", c1, c2);
                            update.addChildUpdate("conflict_error", "text", errorValue);

                            // remove from takenlist

                            deletedCourses.add(c1);
                            deletedCourses.add(c2);

                            errorExists = true;
                        }
                    }
                }

                for (Iterator<Course> iterator = courseTakeAttempt.iterator(); iterator.hasNext(); ) {
                    Course c = iterator.next();
                    if (deletedCourses.contains(c)) {
                        iterator.remove();
                    }
                }


                // else successfully take courses and register to db.
                if (!courseTakeAttempt.isEmpty()) {
                    ArrayList<Course> takenCourses = DbOperation.retrieveTakenCourses(userId, connection);
                    ArrayList<Course> registerCourses = Course.listDifference(courseTakeAttempt, takenCourses);

                    if (!registerCourses.isEmpty()) {
                        System.out.println(Arrays.toString(registerCourses.toArray()));
                        DbOperation.registerSelectedCourses(userId, registerCourses, connection);

                        // update student's tedad vahed
                        int tedadVahed = Integer.parseInt(currentStudent.tedadVahed) + Student.calcTedadVahed(registerCourses);
                        currentStudent.tedadVahed = Integer.toString(tedadVahed);
                        DbOperation.updateStudentByTedadVahed(userId, tedadVahed, connection);

                        // update course's reservedCnt
                        for (Course course : registerCourses) {
                            course.reservedCnt = Integer.toString(Integer.parseInt(course.reservedCnt) + 1);
                            DbOperation.updateCourseByReservedCnt(course.id, Integer.parseInt(course.reservedCnt), connection);
                        }
                    }
                }
            }

            if (errorExists) {
                return update;
            } else {
                view = new SeeCurriculumView();
                CourseList courseList = new CourseList();

                // call repFormat whenever you want to represent courses in xml
                courseList.courses = DbOperation.retrieveTakenCourses(userId, connection);
                Course.convToRepUtility(courseList.courses);
                System.out.println(courseList.courses);

                view.setMustacheModel(courseList);
                return view;
            }


        } else if (updateCommand.startsWith("takecourse")) {

            System.out.println(pageData);
            String courseID = updateCommand.split("/")[1];
            System.out.println("course id: " + courseID);
            Course selectedCourse = DbOperation.retrieveCourse(Course.convToEngNum(courseID), connection);
            System.out.println(selectedCourse);

            if (!courseTakeAttempt.contains(selectedCourse)) {
                courseTakeAttempt.add(selectedCourse);
            }


            return update;
        } else {

            return new HomeView();
        }
//        if (updateCommand.startsWith("doeditpelak")) {
//            selectedid = updateCommand.substring(updateCommand.indexOf("+") + 1);
//            System.out.println(selectedid);
//            EditCar view = new EditCar();
//            pelak = DbOperation.retrievePelak(selectedid, connection);
//            view.setMustacheModel(pelak);
//            selectedtype = pelak.type;
//            selectedharf = pelak.harf;
//            return view;
//        } else if (updateCommand.startsWith("opendeleteask")) {
//            selectedid = updateCommand.substring(updateCommand.indexOf("+") + 1);
//            return new Dialog3();
//        } else if (updateCommand.equals("sabtecarcheck")) {
//            String alert = "";
//            String first = pageData.get("first").toString();
//            if (first.length() != 2) {
//                alert += "\r\nبخش اول پلاک را به صورت عدد دو رقمی وارد کنید";
//                allowmake = false;
//                update.addChildUpdate("first", "text", "");
//            }
//            String second = pageData.get("second").toString();
//            if (second.length() != 3) {
//                alert += "\r\nبخش دوم پلاک را به صورت عدد سه رقمی وارد کنید";
//                allowmake = false;
//                update.addChildUpdate("second", "text", "");
//            }
//            String third = pageData.get("third").toString();
//            if (third.length() != 2) {
//                alert += "\r\nبخش سوم پلاک را به صورت عدد دو رقمی وارد کنید";
//                allowmake = false;
//                update.addChildUpdate("third", "text", "");
//            }
//            String type = selectedtype;
//            if (type.length() == 0) {
//                alert += "\r\nحرف مربوط به پلاک را انتخاب کنید";
//                allowmake = false;
//            }
//            String harf = selectedharf;
//            if (harf.length() == 0) {
//                alert += "\r\nنوع ماشین خود را وارد کنید";
//                allowmake = false;
//            }
//            update.addChildUpdate("alert", "text", alert);
//            if (allowmake) {
//                Pelak pelak = new Pelak();
//                pelak.first = pageData.get("first").toString();
//                pelak.second = pageData.get("second").toString();
//                pelak.third = pageData.get("third").toString();
//                pelak.name = pageData.get("name").toString();
//                pelak.type = selectedtype;
//                pelak.harf = selectedharf;
//                pelak.bedehi = 0;
//                DbOperation.registerPelak(pelak, userId, connection);
//                owner.id = userId;
//                owner.pelaks = DbOperation.retrievePelaks(userId, connection);
//                TarheTraffic view = new TarheTraffic();
//                view.setMustacheModel(owner);
//                allowmake = true;
//                return view;
//            } else {
//                allowmake = true;
//                return update;
//            }
//        } else if (updateCommand.equals("opentype")) {
//            return new Dialog1();
//        } else if (updateCommand.equals("openharf")) {
//            return new Dialog2();
//        } else if (updateCommand.equals("sabtecarcheckedit")) {
//            String alert = "";
//            String first = pageData.get("first").toString();
//            if (first.length() != 2) {
//                alert += "\r\nبخش اول پلاک را به صورت عدد دو رقمی وارد کنید";
//                allowmake = false;
//                update.addChildUpdate("first", "text", "");
//            }
//            String second = pageData.get("second").toString();
//            if (second.length() != 3) {
//                alert += "\r\nبخش دوم پلاک را به صورت عدد سه رقمی وارد کنید";
//                allowmake = false;
//                update.addChildUpdate("second", "text", "");
//            }
//            String third = pageData.get("third").toString();
//            if (third.length() != 2) {
//                alert += "\r\nبخش سوم پلاک را به صورت عدد دو رقمی وارد کنید";
//                allowmake = false;
//                update.addChildUpdate("third", "text", "");
//            }
//            String type = selectedtype;
//            if (type.length() == 0) {
//                alert += "\r\nحرف مربوط به پلاک را انتخاب کنید";
//                allowmake = false;
//            }
//            String harf = selectedharf;
//            if (harf.length() == 0) {
//                alert += "\r\nنوع ماشین خود را وارد کنید";
//                allowmake = false;
//            }
//            update.addChildUpdate("alert", "text", alert);
//            if (allowmake) {
//                Pelak pelak = new Pelak();
//                pelak.first = pageData.get("first").toString();
//                pelak.second = pageData.get("second").toString();
//                pelak.third = pageData.get("third").toString();
//                pelak.name = pageData.get("name").toString();
//                pelak.type = selectedtype;
//                pelak.harf = selectedharf;
//                pelak.id = selectedid;
//                DbOperation.editpelak(pelak, connection);
//                owner.id = userId;
//                owner.pelaks = DbOperation.retrievePelaks(userId, connection);
//                TarheTraffic view = new TarheTraffic();
//                view.setMustacheModel(owner);
//                allowmake = true;
//                return view;
//            } else {
//                allowmake = true;
//                return update;
//            }
//        } else if (updateCommand.equals("الف")) {
//            selectedharf = "الف";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("ب")) {
//            selectedharf = "ب";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("پ")) {
//            selectedharf = "پ";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("ت")) {
//            selectedharf = "ت";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("ث")) {
//            selectedharf = "ث";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("ج")) {
//            selectedharf = "ج";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("چ")) {
//            selectedharf = "چ";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("ح")) {
//            selectedharf = "ح";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("خ")) {
//            selectedharf = "خ";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("د")) {
//            selectedharf = "د";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("ذ")) {
//            selectedharf = "ذ";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("ر")) {
//            selectedharf = "ر";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("ز")) {
//            selectedharf = "ز";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("ژ")) {
//            selectedharf = "ژ";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("س")) {
//            selectedharf = "س";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("ش")) {
//            selectedharf = "ش";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("ص")) {
//            selectedharf = "ص";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("ض")) {
//            selectedharf = "ض";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("ط")) {
//            selectedharf = "ط";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("ظ")) {
//            selectedharf = "ظ";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("ع")) {
//            selectedharf = "ع";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("غ")) {
//            selectedharf = "غ";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("ف")) {
//            selectedharf = "ف";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("ق")) {
//            selectedharf = "ق";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("ک")) {
//            selectedharf = "ک";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("گ")) {
//            selectedharf = "گ";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("ل")) {
//            selectedharf = "ل";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("م")) {
//            selectedharf = "م";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("ن")) {
//            selectedharf = "ن";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("و")) {
//            selectedharf = "و";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("ه")) {
//            selectedharf = "ه";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("ی")) {
//            selectedharf = "ی";
//            update.addChildUpdate("harfhere", "text", selectedharf);
//        } else if (updateCommand.equals("سواری")) {
//            selectedtype = "سواری";
//            update.addChildUpdate("typehere", "text", selectedtype);
//        } else if (updateCommand.equals("وانت و مینی بوس")) {
//            selectedtype = "وانت و مینی بوس";
//            update.addChildUpdate("typehere", "text", selectedtype);
//        } else if (updateCommand.equals("کامیونت")) {
//            selectedtype = "کامیونت";
//            update.addChildUpdate("typehere", "text", selectedtype);
//        } else if (updateCommand.equals("اتوبوس و کامیون دو محور")) {
//            selectedtype = "اتوبوس و کامیون دو محور";
//            update.addChildUpdate("typehere", "text", selectedtype);
//        } else if (updateCommand.equals("کامیون سه محور")) {
//            selectedtype = "کامیون سه محور";
//            update.addChildUpdate("typehere", "text", selectedtype);
//        } else if (updateCommand.equals("تریلی")) {
//            selectedtype = "تریلی";
//            update.addChildUpdate("typehere", "text", selectedtype);
//        } else if (updateCommand.equals("تانکر و نفت کش")) {
//            selectedtype = "تانکر و نفت کش";
//            update.addChildUpdate("typehere", "text", selectedtype);
//        } else if (updateCommand.equals("سواری عمومی")) {
//            selectedtype = "سواری عمومی";
//            update.addChildUpdate("typehere", "text", selectedtype);
//        } else if (updateCommand.equals("سواری بومی")) {
//            selectedtype = "سواری بومی";
//            update.addChildUpdate("typehere", "text", selectedtype);
//        }
//        return update;
//        HomeView homeView = new HomeView();
//        return homeView;
    }
}
