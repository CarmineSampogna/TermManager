package com.csampog.termmanager.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "Assessments")
public class Assessment {

    @PrimaryKey(autoGenerate = true)
    private int assessmentId;
    private String title;
    private AssessmentType testType;
    private Date goalDate;
    private int courseId;

    public static final String OBJECTIVE = "Objective";
    public static final String PERFORMANCE = "Performance";

    public Assessment(){

    }

    public Assessment(String title, AssessmentType testType, Date goalDate, int courseId){

        this.title = title;
        this.testType = testType;
        this.goalDate = goalDate;
        this.courseId = courseId;
    }

    public Assessment(int assessmentId, String title, AssessmentType testType, Date goalDate, int courseId){
        this.assessmentId = assessmentId;
        this.title = title;
        this.testType = testType;
        this.goalDate = goalDate;
        this.courseId = courseId;
    }

    public int getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(int assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getTitle(){

        return this.title;
    }

    public void setTitle(String title){

        this.title = title;
    }

    public AssessmentType getTestType(){

        return  this.testType;
    }

    public void setTestType(AssessmentType testType){

        this.testType = testType;
    }

    public Date getGoalDate(){
        return this.goalDate;
    }

    public void setGoalDate(Date goalDate){

        this.goalDate = goalDate;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public enum AssessmentType{
        OBJECTIVE,
        PERFORMANCE
    }
}
