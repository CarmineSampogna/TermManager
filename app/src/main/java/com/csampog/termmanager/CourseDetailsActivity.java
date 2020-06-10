package com.csampog.termmanager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class CourseDetailsActivity extends AppCompatActivity {

    public static final String COURSE_ID_PARAM = "courseId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);


    }
}