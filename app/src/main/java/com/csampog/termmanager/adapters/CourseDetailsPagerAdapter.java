package com.csampog.termmanager.adapters;

import com.csampog.termmanager.fragments.CourseAssessmentsFragment;
import com.csampog.termmanager.fragments.CourseNotesFragment;
import com.csampog.termmanager.viewmodels.CourseDetailsViewModel;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CourseDetailsPagerAdapter extends FragmentStateAdapter {

    private int courseId = 0;
    CourseDetailsViewModel viewModel;

    public CourseDetailsPagerAdapter(int courseId, CourseDetailsViewModel viewModel, @NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.courseId = courseId;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                CourseAssessmentsFragment caf = CourseAssessmentsFragment.newInstance(courseId, viewModel);
                caf.setViewModel(viewModel);
                return caf;
            case 1:
                CourseNotesFragment cnf = CourseNotesFragment.newInstance(courseId);
                return cnf;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
