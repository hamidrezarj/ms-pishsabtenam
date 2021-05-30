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

import java.sql.Array;
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
    ArrayList<Course> courseDeleteAttempt = new ArrayList<>();

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
        } else if (command.equals("choose_dep")) {
            view = new ChooseDepartment();
            DepartmentList departmentList = new DepartmentList();
            departmentList.departments = DbOperation.retrieveDepList(connection);

            view.setMustacheModel(departmentList);
            return view;
        } else {
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
            System.out.println(Arrays.toString(courseTakeAttempt.toArray()));

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

            // check if no courses have been chosen
            if (courseTakeAttempt.isEmpty()) {
                errorExists = true;
                String error = "هیچ درسی انتخاب نشده است.";
                update.addChildUpdate("max_tedadvahed_error", "text", error);
                return update;
            }

            // if student's maximum tedadvahed reached do sth
            Student currentStudent = DbOperation.retrieveStudent(userId, connection);
            if (Integer.parseInt(currentStudent.tedadVahed) + Student.calcTedadVahed(courseTakeAttempt)
                    > Student.maxTedadVahed) {
                //Render error for maximum vahed reached.
                String error = String.format("تعداد دروس انتخاب شده از سقف %d واحد بیشتر است.", Student.maxTedadVahed);
                update.addChildUpdate("max_tedadvahed_error", "text", error);

                errorExists = true;
            } else {

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


                ArrayList<Course> takenCourses = DbOperation.retrieveTakenCourses(userId, connection);
                if (!takenCourses.isEmpty()) {
                    for (Course c1 : takenCourses) {
                        for (Course c2 : courseTakeAttempt) {

                            if (c1.equals(c2)) {
                                String errorValue = String.format("شما درس %s را قبلا برداشته اید.", c1);
                                update.addChildUpdate("same_courses_error", "text", errorValue);
                                errorExists = true;

                                if (!deletedCourses.contains(c2)) {
                                    deletedCourses.add(c2);
                                }

                                continue;
                            }

                            if (Course.checkTimeConflicts(c1, c2, "_")) {
                                // Render error
                                String errorValue = String.format("دروس %s و %s با هم تداخل دارند.", c1, c2);
                                update.addChildUpdate("conflict_error", "text", errorValue);

                                // remove from takenlist

//                                deletedCourses.add(c1);
                                if (!deletedCourses.contains(c2)) {
                                    deletedCourses.add(c2);
                                }

                                errorExists = true;
                            }

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

                    // empty TakeAttemptList
                    courseTakeAttempt.clear();
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


        } else if (updateCommand.equals("delete_courses")) {

            // first make courseAttempt array
            for (Object obj : pageData.keySet()) {
                String arg = (String) obj;
                if (arg.length() == 10 && arg.contains("_") && pageData.get(arg).equals("true")) {
                    String courseID = arg;
                    Course selectedCourse = DbOperation.retrieveCourse(Course.convToEngNum(courseID), connection);
                    System.out.println(selectedCourse);

                    if (!courseDeleteAttempt.contains(selectedCourse)) {
                        courseDeleteAttempt.add(selectedCourse);
                    }

                }
            }
            boolean errorExists = false;

            // check if no courses have been chosen
            if (courseDeleteAttempt.isEmpty()) {
                errorExists = true;
                String error = "هیچ درسی برای حذف انتخاب نشده است.";
                update.addChildUpdate("nothing_selected_error", "text", error);
                return update;
            }


//                ArrayList<Course> registerCourses = Course.listDifference(courseDeleteAttempt, takenCourses);

//                if (!registerCourses.isEmpty()) {
            System.out.println(Arrays.toString(courseDeleteAttempt.toArray()));
//                DbOperation.registerSelectedCourses(userId, registerCourses, connection);
            DbOperation.deleteSelectedCourses(userId, courseDeleteAttempt, connection);

            // update student's tedad vahed
            Student currentStudent = DbOperation.retrieveStudent(userId, connection);
            int tedadVahed = Integer.parseInt(currentStudent.tedadVahed) - Student.calcTedadVahed(courseDeleteAttempt);
            currentStudent.tedadVahed = Integer.toString(tedadVahed);
            DbOperation.updateStudentByTedadVahed(userId, tedadVahed, connection);

            // update course's reservedCnt
            for (Course course : courseDeleteAttempt) {
                course.reservedCnt = Integer.toString(Integer.parseInt(course.reservedCnt) - 1);
                DbOperation.updateCourseByReservedCnt(course.id, Integer.parseInt(course.reservedCnt), connection);
            }

            // empty TakeAttemptList
            courseDeleteAttempt.clear();


//                }
//                if (errorExists) {
//                    return update;
//                } else {

            // No errors exist. render same view with updated data.
            view = new SeeCurriculumView();
            CourseList courseList = new CourseList();

            // call repFormat whenever you want to represent courses in xml
            courseList.courses = DbOperation.retrieveTakenCourses(userId, connection);
            Course.convToRepUtility(courseList.courses);
            System.out.println(courseList.courses);

            view.setMustacheModel(courseList);
            return view;
//                }


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

    }
}
