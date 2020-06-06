package com.csampog.termmanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csampog.termmanager.R;
import com.csampog.termmanager.model.Term;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

public class TermAdapter extends EntityAdapter<Term, TermAdapter.ViewHolder> {


    public TermAdapter(@NonNull Context context, @NonNull List<Term> entities) {
        super(context, R.layout.term_list_item, entities);
    }

    @Override
    protected void bindEntity(Term term, TermAdapter.ViewHolder viewHolder) {
        TextView title = viewHolder.itemView.findViewById(R.id.term_list_item_title);
        TextView start = viewHolder.itemView.findViewById(R.id.term_list_item_start);
        TextView end = viewHolder.itemView.findViewById(R.id.term_list_item_end);

        SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy");
        title.setText(term.getTitle());
        start.setText(df.format(term.getStart()));
        end.setText(df.format(term.getEnd()));
    }

    @NonNull
    @Override
    public TermAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(viewLayoutId, parent, false);
        return new TermAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public TextView startTextView;
        public TextView endTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.term_list_item_title);
            startTextView = itemView.findViewById(R.id.term_list_item_start);
            endTextView = itemView.findViewById(R.id.term_list_item_end);
        }
    }
}
