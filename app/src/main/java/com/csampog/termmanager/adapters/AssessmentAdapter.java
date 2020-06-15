package com.csampog.termmanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csampog.termmanager.R;
import com.csampog.termmanager.model.Assessment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AssessmentAdapter extends EntityAdapter<Assessment, AssessmentAdapter.AssessmentViewHolder> {


    public AssessmentAdapter(@NonNull Context context, int resource, @NonNull List<Assessment> assessments) {
        super(context, resource, assessments);
    }

    @Override
    protected void bindEntity(Assessment assessment, AssessmentViewHolder viewHolder) {

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
        return 0;
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
