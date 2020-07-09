package com.csampog.termmanager.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.csampog.termmanager.dataAccess.repositories.CourseRepository;
import com.csampog.termmanager.model.Course;
import com.csampog.termmanager.model.Term;

import java.util.List;

public class TermDetailsViewModel extends TermViewModelBase {

    public LiveData<String> title;
    public LiveData<String> formattedStartDate;
    public LiveData<String> formattedEndDate;
    public LiveData<List<Course>> termCourses;
    public LiveData<Boolean> hasCoursesAvailableToAdd;


    private CourseRepository courseRepository;

    private int termId;
    private LiveData<Term> term;
    private LiveData<List<Course>> coursesWithoutTerm;
    private boolean hasCourses = false;

    public TermDetailsViewModel(@NonNull Application application) {
        super(application);
        courseRepository = CourseRepository.getInstance(application.getBaseContext());
        coursesWithoutTerm = courseRepository.getCoursesWithoutTerm();
        hasCoursesAvailableToAdd = Transformations.map(coursesWithoutTerm, courses -> !courses.isEmpty());
    }

    public void updateHasCoursesAvailable() {
        coursesWithoutTerm = courseRepository.getCoursesWithoutTerm();
    }

    public void setTermId(int termId) {

        this.termId = termId;
        LiveData<List<Course>> courses = courseRepository.getCoursesForTerm(this.termId);
    }

    public int getTermId() {

        return this.termId;
    }

    public void refreshTermDetails(int termId) {

        setTermId(termId);
        term = termRepository.getTermById(termId);
        title = Transformations.map(term, t -> t != null ? t.getTitle() : null);
        formattedStartDate = Transformations.map(term, t -> t != null ? dateFormat.format(t.getStart()) : null);
        formattedEndDate = Transformations.map(term, t -> t != null ? dateFormat.format(t.getEnd()) : null);
        termCourses = Transformations.map(courseRepository.getCoursesForTerm(termId), c -> {
            hasCourses = c != null && !c.isEmpty();
            return c;
        });
    }

    public boolean hasCoursesAssigned(){
        return hasCourses;
    }

    public void deleteTerm(){
        if(termId < 1){
            return;
        }

        if(hasCourses){
            throw new RuntimeException("Can't delete a term with courses.");
        }

        Term t = new Term();
        t.setTermId(termId);
        termRepository.delete(t);
    }
}
