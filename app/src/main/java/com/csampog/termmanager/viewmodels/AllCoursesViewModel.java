package com.csampog.termmanager.viewmodels;

import android.app.Application;

import com.csampog.termmanager.dataAccess.repositories.CourseRepository;
import com.csampog.termmanager.messaging.CoursesFilter;
import com.csampog.termmanager.model.Course;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class AllCoursesViewModel extends AndroidViewModel {

    public LiveData<List<Course>> courses;

    private CourseRepository courseRepository;

    public AllCoursesViewModel(@NonNull Application application) {
        super(application);
        courseRepository = CourseRepository.getInstance(application.getBaseContext());
    }

    public void refreshCourses(CoursesFilter filter) {
        switch (filter) {
            case ALL:
                courses = courseRepository.courses;
                break;
            case TERMLESS:
                courses = courseRepository.getCoursesWithoutTerm();
                break;
        }
    }

    public void updateCourse(Course course) {
        if (course == null) {
            return;
        }

        courseRepository.insertOrUpdate(course);
    }
}
