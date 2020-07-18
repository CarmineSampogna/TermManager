package com.csampog.termmanager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;

import com.csampog.termmanager.adapters.AssessmentAdapter;
import com.csampog.termmanager.model.Assessment;
import com.csampog.termmanager.viewmodels.AllAssessmentsViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.List;

public class AllAssessmentsActivity extends AppCompatActivity {

    private List<Assessment> assessments;
    private AssessmentAdapter assessmentAdapter;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_assessments);

        Toolbar toolbar = findViewById(R.id.all_assessments_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NestedScrollView scrollView = findViewById(R.id.assessments_scroll_view);

        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        recyclerView.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.grid_layout_columns)));
        assessments = new ArrayList<>();
        assessmentAdapter = new AssessmentAdapter(this, R.layout.assessment_list_item, assessments, this);
        recyclerView.setAdapter(assessmentAdapter);
        scrollView.addView(recyclerView);

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