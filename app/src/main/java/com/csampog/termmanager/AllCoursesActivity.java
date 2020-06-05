package com.csampog.termmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;

public class AllCoursesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_courses);

        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.coursesToolbar);
        toolbarLayout.setTitle(getString(R.string.courses_title));
    }
}