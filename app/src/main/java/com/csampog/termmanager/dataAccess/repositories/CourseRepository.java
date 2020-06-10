package com.csampog.termmanager.dataAccess.repositories;

import android.content.Context;

import com.csampog.termmanager.dataAccess.utilities.SampleData;
import com.csampog.termmanager.model.Course;

import java.util.List;

import androidx.lifecycle.LiveData;

public class CourseRepository extends RepositoryBase {

    private static CourseRepository instance;

    public static CourseRepository getInstance(Context context) {
        if (instance == null) {
            instance = new CourseRepository(context);
        }
        return instance;
    }

    private CourseRepository(Context context) {
        super(context);
        courses = getAllCourses();
    }

    public void AddSampleData() {

        insertOrUpdateAll(SampleData.getCourses(1, 4));
        insertOrUpdateAll(SampleData.getCourses(0, 2));
    }

    public LiveData<List<Course>> courses;

    public LiveData<Course> getCourseById(final int courseId) {


        return dbContext.courseDao().getCourseById(courseId);
    }

    private LiveData<List<Course>> getAllCourses() {
        return dbContext.courseDao().getAllCourses();
    }

    public LiveData<List<Course>> getCoursesForTerm(int termId) {
        return dbContext.courseDao().getCoursesForTerm(termId);
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

    public LiveData<List<Course>> getCoursesWithoutTerm() {

        return dbContext.courseDao().getCoursesWithoutTerm();
    }
}
