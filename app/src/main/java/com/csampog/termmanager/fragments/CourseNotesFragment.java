package com.csampog.termmanager.fragments;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.csampog.termmanager.NoteActivity;
import com.csampog.termmanager.R;
import com.csampog.termmanager.adapters.NoteAdapter;
import com.csampog.termmanager.model.Note;
import com.csampog.termmanager.viewmodels.CourseDetailsViewModel;
import com.csampog.termmanager.viewmodels.CourseNotesViewModel;

import java.util.ArrayList;
import java.util.List;

public class CourseNotesFragment extends Fragment {

    private CourseDetailsViewModel mViewModel;
    private Button addNoteButton;
    private int courseId;
    private List<Note> courseNotes;
    private RecyclerView notesRecyclerView;
    private NoteAdapter adapter;

    public static CourseNotesFragment newInstance(int courseId) {

        CourseNotesFragment fragment = new CourseNotesFragment();
        Bundle args = new Bundle();
        args.putInt("courseId", courseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courseId = getArguments().getInt("courseId");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.course_notes_fragment, container, false);

        mViewModel = new ViewModelProvider(getViewModelStore(), new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()))
                .get(CourseDetailsViewModel.class);
        mViewModel.setCourseId(courseId);

        addNoteButton = view.findViewById(R.id.add_note_button);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NoteActivity.class);
                intent.putExtra(NoteActivity.IS_READ_ONLY_PARAM, false);
                intent.putExtra(NoteActivity.COURSE_ID_PARAM, courseId);
                startActivity(intent);
            }
        });

        notesRecyclerView = view.findViewById(R.id.course_notes_recyclerView);
        notesRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.grid_layout_columns)));

        courseNotes = new ArrayList<>();
        adapter = new NoteAdapter(getContext(), R.layout.note_list_item, courseNotes, getActivity());
        notesRecyclerView.setAdapter(adapter);

        final Observer<List<Note>> courseNotesObserver = new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                courseNotes.clear();
                courseNotes.addAll(notes);
                if(adapter == null){
                    adapter = new NoteAdapter(getContext(), R.layout.note_list_item, courseNotes, getActivity());
                    notesRecyclerView.setAdapter(adapter);
                }else{
                    adapter.notifyDataSetChanged();
                }
            }
        };

        mViewModel.courseNotes.observe(getViewLifecycleOwner(), courseNotesObserver);
        return view;
    }
}