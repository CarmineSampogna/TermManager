package com.csampog.termmanager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csampog.termmanager.adapters.AssessmentAdapter;
import com.csampog.termmanager.model.Assessment;
import com.csampog.termmanager.viewmodels.AllAssessmentsViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.List;

public class AllAssessmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Assessment> assessments;
    private AssessmentAdapter assessmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_assessments);

        recyclerView = findViewById(R.id.all_assessments_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.grid_layout_columns)));
        assessments = new ArrayList<>();
        assessmentAdapter = new AssessmentAdapter(this, R.layout.assessment_list_item, assessments, this);
        recyclerView.setAdapter(assessmentAdapter);

        CollapsingToolbarLayout tbl = findViewById(R.id.assessments_toolbar);
        tbl.setTitle(getString(R.string.assessments_title));

        final Observer<List<Assessment>> assessmentsObserver = aList -> {
            assessments.clear();
            assessments.addAll(aList);
            assessmentAdapter.notifyDataSetChanged();
        };

        AllAssessmentsViewModel viewModel = new ViewModelProvider(getViewModelStore(), new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(AllAssessmentsViewModel.class);
        viewModel.assessments.observe(this, assessmentsObserver);
    }
}