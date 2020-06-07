package com.csampog.termmanager.dataAccess.utilities;

import com.csampog.termmanager.model.Course;
import com.csampog.termmanager.model.Term;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SampleData {

    public static List<Term> getTerms(int count) {
        List<Term> ret = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            int num = i + 1;
            String title = "Term " + num;
            Date start = Date.from(Instant.now().plus(i * 180, ChronoUnit.DAYS));
            Date end = Date.from(start.toInstant().plus(180, ChronoUnit.DAYS));
            ret.add(new Term(num, title, start, end));
        }

        return ret;
    }

    public static List<Course> getCourses(int count) {

        List<Course> ret = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            int num = i + 1;
            String title = "Course " + num;
            Date start = Date.from(Instant.now().plus(i * 60, ChronoUnit.DAYS));
            Date end = Date.from(start.toInstant().plus(60, ChronoUnit.DAYS));
            ret.add(new Course(num, title, start, end, Course.PLAN_TO_TAKE, "", "", ""));
        }

        return ret;
    }

    public static List<Course> getCourses(int termId, int count) {
        List<Course> courses = getCourses(count);
        for (Course c : courses) {
            c.setTermId(termId);
        }

        return courses;
    }
}
