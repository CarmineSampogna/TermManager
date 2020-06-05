package com.csampog.termmanager.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.Date;

@Entity(tableName = "Terms")
public class Term {

    @PrimaryKey(autoGenerate = true)
    private int termId;
    private String title;
    private Date start;
    private Date end;

    @Ignore
    public Term(){

    }

    @Ignore
    public Term(String title, Date start, Date end){

        this.title = title;
        this.start = start;
        this.end = end;
    }

    public Term(int termId, String title, Date start, Date end){

        this.termId = termId;
        this.title = title;
        this.start = start;
        this.end = end;
    }

    public void setTermId(int termId) {

        this.termId = termId;
    }

    public int getTermId() {

        return termId;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public String getTitle() {

        return title;
    }

    public Date getEnd() {

        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Date getStart() {

        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }
}
