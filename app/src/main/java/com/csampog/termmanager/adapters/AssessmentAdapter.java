package com.csampog.termmanager.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.csampog.termmanager.AddAssessmentActivity;
import com.csampog.termmanager.EditAssessmentActivity;
import com.csampog.termmanager.R;
import com.csampog.termmanager.dataAccess.repositories.AssessmentRepository;
import com.csampog.termmanager.model.Assessment;

import java.text.SimpleDateFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AssessmentAdapter extends EntityAdapter<Assessment, AssessmentAdapter.AssessmentViewHolder> {

    private Activity activity;

    public AssessmentAdapter(@NonNull Context context, int resource, @NonNull List<Assessment> assessments, Activity activity) {
        super(context, resource, assessments);
        this.activity = activity;
    }

    @Override
    protected void bindEntity(Assessment assessment, AssessmentViewHolder viewHolder) {

        SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy");
        viewHolder.nameTextView.setText(assessment.getTitle());
        viewHolder.assessmentTypeTextView.setText(assessment.getTestType());
        viewHolder.goalDateTextView.setText(df.format(assessment.getGoalDate()));

        viewHolder.optionsMenuTextView.setOnClickListener(v -> {

            PopupMenu popupMenu = new PopupMenu(viewHolder.itemView.getContext(), v);
            popupMenu.inflate(R.menu.assessment_list_item_menu);
            popupMenu.setOnMenuItemClickListener(item -> {

                if (item.getItemId() == R.id.assessment_delete_menu_item) {
                    AssessmentRepository.getInstance(v.getContext())
                            .delete(assessment);
                }
                return false;
            });
            popupMenu.show();
        });

        viewHolder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(activity, EditAssessmentActivity.class);
            intent.putExtra(AddAssessmentActivity.COURSE_ID_PARAM, assessment.getCourseId());
            intent.putExtra(AddAssessmentActivity.ASSESSMENT_ID_PARAM, assessment.getAssessmentId());
            activity.startActivity(intent);
        });
    }


    @NonNull
    @Override
    public AssessmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.assessment_list_item, parent, false);
        return new AssessmentViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    public class AssessmentViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public TextView assessmentTypeTextView;
        public TextView goalDateTextView;
        public TextView optionsMenuTextView;

        public AssessmentViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.assessment_list_item_title);
            assessmentTypeTextView = itemView.findViewById(R.id.assessment_list_item_type);
            goalDateTextView = itemView.findViewById(R.id.assessment_list_item_start);
            optionsMenuTextView = itemView.findViewById(R.id.assessment_list_item_option);
        }
    }
}
