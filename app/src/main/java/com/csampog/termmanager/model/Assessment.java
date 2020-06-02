package com.csampog.termmanager.model;

import java.time.LocalDate;

public class Assessment {

    private int assessmentId;
    private String title;
    private int testType;
    private LocalDate goalDate;
    private int courseId;

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

    public  int getTestType(){
        return  this.testType;
    }

    public void setTestType(int testType){
        this.testType = testType;
    }

    public LocalDate getGoalDate(){
        return this.goalDate;
    }

    public void setGoalDate(LocalDate goalDate){
        this.goalDate = goalDate;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}
