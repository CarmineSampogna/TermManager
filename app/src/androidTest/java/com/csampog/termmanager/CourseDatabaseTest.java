package com.csampog.termmanager;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import com.csampog.termmanager.dataAccess.interfaces.CourseDao;
import com.csampog.termmanager.dataAccess.TermManagerDbContext;
import com.csampog.termmanager.model.Course;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@RunWith(JUnit4.class)
public class CourseDatabaseTest {
    private final String TAG = "JUnit";
    private TermManagerDbContext dbContext;
    private CourseDao courseDao;

    @Before
    public void createDb(){
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        dbContext = Room.inMemoryDatabaseBuilder(context, TermManagerDbContext.class)
                .build();
        courseDao = dbContext.courseDao();
        Log.i(TAG, "createDb");
    }

    @After
    public void closeDb(){
        dbContext.close();
        Log.i(TAG, "closeDb");
    }

    @Test
    public void createAndRetrieveCourse(){
        Course course = new Course(1, "Test Title", Date.from(Instant.now()), Date.from(Instant.now()), Course.PLAN_TO_TAKE, "Test name", "Test phone", "Test email");
        courseDao.insertOrUpdate(course);

        Course dbCourse = courseDao.getCourseById(1);
        Assert.assertNotNull(dbCourse);
    }

    @Test
    public void deleteCourse(){
        Course course = new Course(1, "Test Title", Date.from(Instant.now()), Date.from(Instant.now()), Course.PLAN_TO_TAKE, "Test name", "Test phone", "Test email");
        courseDao.insertOrUpdate(course);

        Course dbCourse = courseDao.getCourseById(1);
        Assert.assertNotNull(dbCourse);

        courseDao.delete(dbCourse);
        dbCourse = courseDao.getCourseById(1);
        Assert.assertNull(dbCourse);
    }

    @Test
    public void modifyCourse(){

        String expectedName = "MODIFIED NAME";
        String expectedStatus = Course.DROPPED;
        Date expectedStartDate = Date.from(Instant.now().plus(2, ChronoUnit.HOURS));
        Date expectedEndDate = Date.from(Instant.now().plus(6, ChronoUnit.HOURS));
        String modifiedMentor = "Modified Mentor";
        String modifiedMentorPhone = "Modified mentor phone";
        String modifiedMentorEmail = "Modified mentor email";


        Course course = new Course(1, "Test Title", Date.from(Instant.now()), Date.from(Instant.now()), Course.PLAN_TO_TAKE, "Test name", "Test phone", "Test email");
        courseDao.insertOrUpdate(course);

        Course dbCourse = courseDao.getCourseById(1);
        dbCourse.setTitle(expectedName);
        dbCourse.setStatus(expectedStatus);
        dbCourse.setAnticipatedEndDate(expectedEndDate);
        dbCourse.setStartDate(expectedStartDate);
        dbCourse.setMentorName(modifiedMentor);
        dbCourse.setMentorPhone(modifiedMentorPhone);
        dbCourse.setMentorEmail(modifiedMentorEmail);
        courseDao.insertOrUpdate(dbCourse);

        Course modifiedCourse = courseDao.getCourseById(1);
        Assert.assertEquals(expectedName, modifiedCourse.getTitle());
        Assert.assertEquals(expectedStatus, modifiedCourse.getStatus());
        Assert.assertEquals(expectedStartDate, modifiedCourse.getStartDate());
        Assert.assertEquals(expectedEndDate, modifiedCourse.getAnticipatedEndDate());
        Assert.assertEquals(modifiedMentor, modifiedCourse.getMentorName());
        Assert.assertEquals(modifiedMentorPhone, modifiedCourse.getMentorPhone());
        Assert.assertEquals(modifiedMentorEmail, modifiedCourse.getMentorEmail());
    }
}
