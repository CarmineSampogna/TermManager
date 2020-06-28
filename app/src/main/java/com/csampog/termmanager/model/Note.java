package com.csampog.termmanager.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "Notes", indices = {@Index("courseId")})
public class Note {

    @PrimaryKey(autoGenerate = true)
    private int noteId;
    private String title;
    private String text;
    private int courseId;

    @Ignore
    public Note(){

    }

    @Ignore
    public Note(String title, String text, int courseId){
        this.title = title;
        this.text = text;
        this.courseId = courseId;
    }

    public Note(int noteId, String title, String text, int courseId){
        this.title = title;
        this.noteId = noteId;
        this.text = text;
        this.courseId = courseId;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getCourseId() {
        return courseId;
    }
}
