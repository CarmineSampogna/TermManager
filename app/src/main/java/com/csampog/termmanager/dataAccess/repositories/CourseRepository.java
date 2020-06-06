package com.csampog.termmanager.dataAccess.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.csampog.termmanager.model.Course;

import java.util.List;

public class CourseRepository extends RepositoryBase {

    public CourseRepository(Context context) {
        super(context);
    }

    public Course getCourseById(final int courseId) {
        final Course[] ret = {null};

        executor.execute(new Runnable() {
            @Override
            public void run() {
                ret[0] = dbContext.courseDao().getCourseById(courseId);
            }
        });

        return ret[0];
    }

    public LiveData<List<Course>> getAllCourses() {
        return dbContext.courseDao().getAllCourses();
    }

    public void insertOrUpdate(Course course) {
        if (course == null) throw new NullPointerException(Course.class.getName());
        final Course fCourse = course;

        executor.execute(new Runnable() {
            @Override
            public void run() {
                dbContext.courseDao().insertOrUpdate(fCourse);
            }
        });
    }

    public void insertOrUpdateAll(List<Course> courses) {
        if (courses == null) throw new NullPointerException(Course.class.getName());
        final List<Course> fCourses = courses;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                dbContext.courseDao().insertOrUpdateAll(fCourses);
            }
        });
    }

    public void delete(final Course course) {
        if (course == null) return;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                dbContext.courseDao().delete(course);
            }
        });
    }
}
