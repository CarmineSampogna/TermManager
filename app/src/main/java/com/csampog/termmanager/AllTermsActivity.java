package com.csampog.termmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.csampog.termmanager.adapters.TermAdapter;
import com.csampog.termmanager.dataAccess.repositories.TermRepository;
import com.csampog.termmanager.model.Term;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class AllTermsActivity extends AppCompatActivity {

    List<Term> termsData;
    RecyclerView recyclerView;
    TermAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_terms);
        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.all_terms_toolbar_layout);
        toolbarLayout.setTitle(getString(R.string.all_terms_title));

        recyclerView = findViewById(R.id.termsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
        termsData = new ArrayList<>();

        adapter = new TermAdapter(AllTermsActivity.this, termsData);
        recyclerView.setAdapter(adapter);
        final TermRepository repo = TermRepository.getInstance(getApplicationContext());

        final Observer<List<Term>> termObserver = new Observer<List<Term>>() {
            @Override
            public void onChanged(List<Term> terms) {
                //termsData.clear();
                termsData.addAll(terms);
                if(adapter == null) {
                    adapter = new TermAdapter(AllTermsActivity.this, termsData);
                    recyclerView.setAdapter(adapter);
                }
                else{
                    adapter.notifyDataSetChanged();
                }
            }
        };

        repo.terms.observe(this, termObserver);

        FloatingActionButton fab = findViewById(R.id.fab_add_term);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(termsData.size() == 0){
                    repo.AddSampleData();
                }
                Intent intent = new Intent(AllTermsActivity.this, AddTermActivity.class);
                startActivity(intent);
            }
        });
    }
}