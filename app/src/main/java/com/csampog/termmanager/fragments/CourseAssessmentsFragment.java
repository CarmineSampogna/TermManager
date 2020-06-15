package com.csampog.termmanager.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csampog.termmanager.AssessmentDetailsActivity;
import com.csampog.termmanager.R;
import com.csampog.termmanager.adapters.AssessmentAdapter;
import com.csampog.termmanager.model.Assessment;
import com.csampog.termmanager.viewmodels.CourseDetailsViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CourseAssessmentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseAssessmentsFragment extends Fragment {

    private static final String COURSE_ID_PARAM = "courseId";

    private RecyclerView courseAssessmentsRecyclerView;
    private AssessmentAdapter assessmentAdapter;
    private int courseId;
    private CourseDetailsViewModel viewModel;
    private List<Assessment> assessments;
    private MaterialButton addAssessmentButton;

    public CourseAssessmentsFragment() {
        // Required empty public constructor
    }


    public static CourseAssessmentsFragment newInstance(int courseId, CourseDetailsViewModel viewModel) {
        CourseAssessmentsFragment fragment = new CourseAssessmentsFragment();
        Bundle args = new Bundle();
        args.putInt(COURSE_ID_PARAM, courseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courseId = getArguments().getInt(COURSE_ID_PARAM);
        }
    }

    public void setViewModel(CourseDetailsViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_course_assessments, container, false);

        addAssessmentButton = view.findViewById(R.id.add_assessment_button);
        addAssessmentButton.setOnClickListener(v -> {
            Intent addAssessmentIntent = new Intent(getContext(), AssessmentDetailsActivity.class);
            startActivity(addAssessmentIntent);
        });

        initRecyclerView(view);
        return view;
    }

    private void initRecyclerView(View view) {

        assessments = new ArrayList<>();
        courseAssessmentsRecyclerView = view.findViewById(R.id.course_assessments_recyclerView);
        courseAssessmentsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.grid_layout_columns)));
        final Observer<List<Assessment>> assessmentsObserver = assessments -> {
            assessments.clear();
            assessments.addAll(assessments);

            if (assessmentAdapter == null) {
                assessmentAdapter = new AssessmentAdapter(getContext(), R.layout.assessment_list_item, assessments);
            } else {
                assessmentAdapter.notifyDataSetChanged();
            }
        };

        viewModel.courseAssessments.observe(getViewLifecycleOwner(), assessmentsObserver);

        assessmentAdapter = new AssessmentAdapter(getContext(), R.layout.assessment_list_item, assessments);
        courseAssessmentsRecyclerView.setAdapter(assessmentAdapter);
    }
}