package com.csampog.termmanager.dataAccess.interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.csampog.termmanager.model.Course;

import java.util.List;

@Dao
public interface CourseDao extends EntityDao<Course> {

    @Query("SELECT * FROM COURSES WHERE courseId = :courseId")
    Course getCourseById(int courseId);

    @Query("SELECT * FROM Courses")
    LiveData<List<Course>> getAllCourses();

}
