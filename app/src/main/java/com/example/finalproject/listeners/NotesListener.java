package com.example.finalproject.listeners;

import com.example.finalproject.entities.Note;

import java.util.ArrayList;
import java.util.List;

public interface NotesListener {
    void onNoteClicked(Note note,int position);
    void onNoteLongClicked(List<Note> notes);

}
