package com.csampog.termmanager.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.csampog.termmanager.dataAccess.repositories.AssessmentRepository;
import com.csampog.termmanager.dataAccess.repositories.NoteRepository;
import com.csampog.termmanager.model.Assessment;
import com.csampog.termmanager.model.Course;
import com.csampog.termmanager.model.Note;

import java.util.List;

public class CourseDetailsViewModel extends CourseViewModelBase {

    public LiveData<String> title;
    public LiveData<String> formattedStartDate;
    public LiveData<String> formattedEndDate;
    public LiveData<String> status;
    public LiveData<Boolean> hasMentorInfo;
    public LiveData<String> mentorName;
    public LiveData<String> mentorPhone;
    public LiveData<String> mentorEmail;
    public LiveData<List<Assessment>> courseAssessments;
    public LiveData<List<Note>> courseNotes;
    public LiveData<Boolean> startAlertsEnabled;
    public LiveData<Boolean> endAlertsEnabled;


    private AssessmentRepository assessmentRepository;
    private NoteRepository noteRepository;

    private int courseId;
    private LiveData<Course> course;

    public CourseDetailsViewModel(@NonNull Application application) {
        super(application);
        assessmentRepository = AssessmentRepository.getInstance(application);
        noteRepository = NoteRepository.getInstance(application);
    }

    public void setCourseId(int courseId) {
        if (courseId < 1) throw new IllegalArgumentException();

        this.courseId = courseId;
        courseAssessments = assessmentRepository.getAssessmentsForCourse(courseId);
        courseNotes = noteRepository.getNotesForCourse(courseId);
    }

    public int getCourseId() {

        return this.courseId;
    }

    public void refreshCourseDetails(int courseId) {

        setCourseId(courseId);
        course = courseRepository.getCourseById(courseId);
        title = Transformations.map(course, c -> c != null ? c.getTitle() : "");
        formattedStartDate = Transformations.map(course, c -> c != null ? dateFormat.format(c.getStartDate()) : "");
        formattedEndDate = Transformations.map(course, c -> c != null ? dateFormat.format(c.getAnticipatedEndDate()) : "");
        status = Transformations.map(course, c -> c != null ? c.getStatus() : "");
        startAlertsEnabled = Transformations.map(course, c -> c != null ? c.getStartAlertEnabled() : false);
        endAlertsEnabled = Transformations.map(course, c -> c != null ? c.getEndAlertEnabled() : false);
        hasMentorInfo = Transformations.map(course, c -> {

            if (c == null) return false;

            String mName = c.getMentorName();
            String mPhone = c.getMentorPhone();
            String mEmail = c.getMentorEmail();

            return (mName != null && !mName.isEmpty()) ||
                    (mPhone != null && !mPhone.isEmpty()) ||
                    (mEmail != null && !mEmail.isEmpty());
        });
        mentorName = Transformations.map(course, c -> c != null ? c.getMentorName() : "");
        mentorPhone = Transformations.map(course, c -> c != null ? c.getMentorPhone() : "");
        mentorEmail = Transformations.map(course, c -> c != null ? c.getMentorEmail() : "");
        courseAssessments = assessmentRepository.getAssessmentsForCourse(courseId);
        courseNotes = noteRepository.getNotesForCourse(courseId);
    }

    public void deleteCourse() {
        Course course = new Course();
        course.setCourseId(courseId);
        courseRepository.delete(course);
    }
}
