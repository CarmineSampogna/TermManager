package com.csampog.termmanager;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import com.csampog.termmanager.dataAccess.AssessmentDao;
import com.csampog.termmanager.dataAccess.CourseDao;
import com.csampog.termmanager.dataAccess.TermManagerDbContext;
import com.csampog.termmanager.model.Assessment;
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
public class AssessmentDatabaseTest {
    private final String TAG = "JUnit";
    private TermManagerDbContext dbContext;
    private AssessmentDao assessmentDao;

    @Before
    public void createDb(){
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        dbContext = Room.inMemoryDatabaseBuilder(context, TermManagerDbContext.class)
                .build();
        assessmentDao = dbContext.assessmentDao();
        Log.i(TAG, "createDb");
    }

    @After
    public void closeDb(){
        dbContext.close();
        Log.i(TAG, "closeDb");
    }

    @Test
    public void insertAndRetrieveAssessment(){
        Course course = new Course(1, "Test Title", Date.from(Instant.now()), Date.from(Instant.now()), Course.PLAN_TO_TAKE, "Test name", "Test phone", "Test email");
        dbContext.courseDao().insertOrUpdate(course);

        Assessment assessment = new Assessment(1, "Test", Assessment.AssessmentType.PERFORMANCE, Date.from(Instant.now()), 1);
        assessmentDao.insertOrUpdate(assessment);

        Assessment dbAssessment = assessmentDao.getAssessmentById(1);
        Assert.assertNotNull(dbAssessment);
        Assert.assertEquals(1, dbAssessment.getCourseId());
    }

    @Test
    public void deleteAssessment(){
        Course course = new Course(1, "Test Title", Date.from(Instant.now()), Date.from(Instant.now()), Course.PLAN_TO_TAKE, "Test name", "Test phone", "Test email");
        dbContext.courseDao().insertOrUpdate(course);

        Assessment assessment = new Assessment(1, "Test", Assessment.AssessmentType.PERFORMANCE, Date.from(Instant.now()), 1);
        assessmentDao.insertOrUpdate(assessment);

        Assessment dbAssessment = assessmentDao.getAssessmentById(1);
        Assert.assertNotNull(dbAssessment);
        assessmentDao.deleteAssessment(dbAssessment);

        dbAssessment = assessmentDao.getAssessmentById(1);
        Assert.assertNull(dbAssessment);
    }

    @Test
    public void modifyAssessment(){

        String expectedTitle = "Modified title";
        Date expectedDate = Date.from(Instant.now().plus(6, ChronoUnit.DAYS));
        Assessment.AssessmentType expectedAssessmentType = Assessment.AssessmentType.OBJECTIVE;

        Course course = new Course(1, "Test Title", Date.from(Instant.now()), Date.from(Instant.now()), Course.PLAN_TO_TAKE, "Test name", "Test phone", "Test email");
        dbContext.courseDao().insertOrUpdate(course);

        Assessment assessment = new Assessment(1, "Test", Assessment.AssessmentType.PERFORMANCE, Date.from(Instant.now()), 1);
        assessmentDao.insertOrUpdate(assessment);

        Assessment dbAssessment = assessmentDao.getAssessmentById(1);
        Assert.assertNotNull(dbAssessment);

        dbAssessment.setTitle(expectedTitle);
        dbAssessment.setGoalDate(expectedDate);
        dbAssessment.setTestType(expectedAssessmentType);
        assessmentDao.insertOrUpdate(dbAssessment);

        Assessment modifiedAssessment = assessmentDao.getAssessmentById(1);
        Assert.assertNotNull(modifiedAssessment);
        Assert.assertEquals(expectedTitle, modifiedAssessment.getTitle());
        Assert.assertEquals(expectedDate, modifiedAssessment.getGoalDate());
        Assert.assertEquals(expectedAssessmentType, modifiedAssessment.getTestType());
    }
}
