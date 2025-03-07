package com.csampog.termmanager.dataAccess.interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.csampog.termmanager.model.Course;

import java.util.List;

@Dao
public interface CourseDao extends EntityDao<Course> {

    @Query("SELECT * FROM COURSES WHERE courseId = :courseId")
    LiveData<Course> getCourseById(int courseId);

    @Query("SELECT * FROM Courses")
    LiveData<List<Course>> getAllCourses();

    @Query("SELECT * FROM COURSES WHERE termId = :termId")
    LiveData<List<Course>> getCoursesForTerm(int termId);

    @Query("SELECT * FROM COURSES WHERE termId = 0")
    LiveData<List<Course>> getCoursesWithoutTerm();

    @Query("SELECT * FROM COURSES WHERE startAlertEnabled = 1 OR endAlertEnabled = 1")
    List<Course> getCoursesForAlerts();

}
