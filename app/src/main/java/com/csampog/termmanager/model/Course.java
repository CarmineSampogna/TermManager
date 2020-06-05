package com.csampog.termmanager.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.Date;

@Entity(tableName = "Courses")
public class Course {

    @PrimaryKey(autoGenerate = true)
    private int courseId;
    private String title;
    private Date startDate;
    private Date anticipatedEndDate;
    private String status;
    private String mentorName;
    private String mentorPhone;
    private String mentorEmail;
    private int termId;

    public static final String IN_PROGRESS;
    public static final String COMPLETED;
    public static final String DROPPED;
    public static final String PLAN_TO_TAKE;

    static {
        IN_PROGRESS = "In progress";
        COMPLETED = "Completed";
        DROPPED = "Dropped";
        PLAN_TO_TAKE = "Plan to take";
    }


    @Ignore
    public Course(){

    }

    @Ignore
    public Course(String title, Date startDate, Date anticipatedEndDate, String status, String mentorName, String mentorPhone, String mentorEmail){

        this.title = title;
        this.startDate = startDate;
        this.anticipatedEndDate = anticipatedEndDate;
        this.status = status;
        this.mentorName = mentorName;
        this.mentorPhone = mentorPhone;
        this.mentorEmail = mentorEmail;
    }

    public Course(int courseId, String title, Date startDate, Date anticipatedEndDate, String status, String mentorName, String mentorPhone, String mentorEmail){
        this.courseId = courseId;
        this.title = title;
        this.startDate = startDate;
        this.anticipatedEndDate = anticipatedEndDate;
        this.status = status;
        this.mentorName = mentorName;
        this.mentorPhone = mentorPhone;
        this.mentorEmail = mentorEmail;
    }

    public int getTermId() {
        return termId;
    }

    public void setTermId(int termId) {
        this.termId = termId;
    }

    public String getMentorEmail() {
        return mentorEmail;
    }

    public void setMentorEmail(String mentorEmail) {
        this.mentorEmail = mentorEmail;
    }

    public String getMentorPhone() {
        return mentorPhone;
    }

    public void setMentorPhone(String mentorPhone) {
        this.mentorPhone = mentorPhone;
    }

    public String getMentorName() {
        return mentorName;
    }

    public void setMentorName(String mentorName) {
        this.mentorName = mentorName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getAnticipatedEndDate() {
        return anticipatedEndDate;
    }

    public void setAnticipatedEndDate(Date anticipatedEndDate) {
        this.anticipatedEndDate = anticipatedEndDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}
