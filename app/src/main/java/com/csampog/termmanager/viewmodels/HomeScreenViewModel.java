package com.csampog.termmanager.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.csampog.termmanager.dataAccess.repositories.AssessmentRepository;
import com.csampog.termmanager.dataAccess.repositories.CourseRepository;
import com.csampog.termmanager.dataAccess.repositories.TermRepository;
import com.csampog.termmanager.model.Assessment;
import com.csampog.termmanager.model.Course;
import com.csampog.termmanager.model.Term;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class HomeScreenViewModel extends AndroidViewModel {

    public LiveData<Optional<Term>> latestTerm;
    public LiveData<Boolean> hasTerms;

    public LiveData<Optional<Course>> latestCourse;
    public LiveData<Boolean> hasCourses;

    public LiveData<Assessment> latestAssessment;
    public LiveData<Boolean> hasAssessments;

    private List<Term> terms;
    private List<Course> courses;
    private List<Assessment> assessments;

    private TermRepository termRepository;
    private CourseRepository courseRepository;
    private AssessmentRepository assessmentRepository;

    public HomeScreenViewModel(@NonNull Application application) {
        super(application);

        terms = new ArrayList<>();
        courses = new ArrayList<>();
        assessments = new ArrayList<>();

        termRepository = TermRepository.getInstance(application);
        courseRepository = CourseRepository.getInstance(application);
        assessmentRepository = AssessmentRepository.getInstance(application);
    }

    public void init(){

        initTerm();

        latestCourse = Transformations.map(courseRepository.courses, c -> c.stream().sorted((o1, o2) -> {
            if(o1.getStartDate().before(o2.getStartDate())){
                return -1;
            }

            if(o1.getStartDate().after(o2.getStartDate())){
                return 1;
            }

            return 0;
        }).filter(t2 -> t2.getStatus() == Course.IN_PROGRESS)
                .findFirst());

        hasCourses = Transformations.map(courseRepository.courses, c -> !c.isEmpty());

        Transformations.map(courseRepository.courses, c -> courses.addAll(c));

        Transformations.map(assessmentRepository.getAllAssessments(), a -> assessments.addAll(a));
    }

    private void initTerm() {
        latestTerm = Transformations.map(termRepository.terms, t -> t.stream().sorted((o1, o2) -> {
            if(o1.getStart().before(o2.getStart())){
                return -1;
            }

            if(o1.getStart().after(o2.getStart())){
                return 1;
            }

            return 0;
        }).filter(t2 -> !t2.getEnd().before(Date.from(Instant.now())))
        .findFirst());

        hasTerms = Transformations.map(termRepository.terms, t -> !t.isEmpty());
    }
}
