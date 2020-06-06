package com.csampog.termmanager.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.csampog.termmanager.R;
import com.csampog.termmanager.model.Term;

import androidx.annotation.NonNull;

public class TermAdapter extends EntityAdapter<Term> {


    public TermAdapter(@NonNull Context context, @NonNull Term[] objects) {
        super(context, R.layout.term_list_item, objects);
    }

    @Override
    protected void bindEntity(Term term, View view) {
        TextView title = view.findViewById(R.id.term_list_item_title);
        TextView start = view.findViewById(R.id.term_list_item_start);
        TextView end = view.findViewById(R.id.term_list_item_end);

        title.setText(term.getTitle());
        start.setText(term.getStart().toString());
        end.setText(term.getEnd().toString());
    }
}
