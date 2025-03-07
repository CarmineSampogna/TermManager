package com.csampog.termmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.csampog.termmanager.adapters.TermAdapter;
import com.csampog.termmanager.model.Term;
import com.csampog.termmanager.viewmodels.AllTermsViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AllTermsActivity extends AppCompatActivity {

    private List<Term> termsData;
    private RecyclerView recyclerView;
    private TermAdapter adapter;
    private AllTermsViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_terms);


        setTitle();
        initRecyclerView();
        initViewModel();
        initFAB();
    }

    private void setTitle() {

        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.all_terms_toolbar_layout);
        toolbarLayout.setTitle(getString(R.string.all_terms_title));
        Toolbar toolbar = findViewById(R.id.all_terms_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initRecyclerView() {

        initAdapter();
        recyclerView = findViewById(R.id.termsRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.grid_layout_columns)));
        recyclerView.setAdapter(adapter);
    }

    private void initAdapter() {

        termsData = new ArrayList<>();
        adapter = new TermAdapter(this, termsData, this);
    }

    private void initViewModel() {

        viewModel = new ViewModelProvider(getViewModelStore(), new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(AllTermsViewModel.class);

        final Observer<List<Term>> termObserver = new Observer<List<Term>>() {
            @Override
            public void onChanged(List<Term> terms) {
                termsData.clear();
                termsData.addAll(terms);
                if (adapter == null) {
                    adapter = new TermAdapter(AllTermsActivity.this, termsData, AllTermsActivity.this);
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
        };

        viewModel.allTerms.observe(this, termObserver);
    }

    private void initFAB() {
        FloatingActionButton fab = findViewById(R.id.fab_add_term);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllTermsActivity.this, AddTermActivity.class);
                startActivity(intent);
            }
        });
    }
}