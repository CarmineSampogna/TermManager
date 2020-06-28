package com.csampog.termmanager.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csampog.termmanager.NoteActivity;
import com.csampog.termmanager.R;
import com.csampog.termmanager.model.Note;

import java.util.List;

public class NoteAdapter extends EntityAdapter<Note, NoteAdapter.ViewHolder> {

    private Activity activity;

    public NoteAdapter(@NonNull Context context, int resource, @NonNull List<Note> notes, Activity activity) {
        super(context, resource, notes);
        this.activity = activity;
    }


    @Override
    protected void bindEntity(Note note, ViewHolder viewHolder) {
        viewHolder.titleTextView.setText(note.getTitle());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, NoteActivity.class);
                i.putExtra(NoteActivity.COURSE_ID_PARAM, note.getCourseId());
                i.putExtra(NoteActivity.NOTE_ID_PARAM, note.getNoteId());
                i.putExtra(NoteActivity.IS_READ_ONLY_PARAM, false);
                i.putExtra(NoteActivity.IS_NEW_NOTE_PARAM, false);
                activity.startActivity(i);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.note_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.note_list_item_title_text);
        }
    }
}
