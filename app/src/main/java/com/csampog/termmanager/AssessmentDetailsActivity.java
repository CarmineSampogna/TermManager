package com.csampog.termmanager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AssessmentDetailsActivity extends AppCompatActivity {

    public static final String ASSESSMENT_ID_PARAM = "assessmentId";
    public static final String COURSE_NAME_PARAM = "courseName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_details);
    }
}