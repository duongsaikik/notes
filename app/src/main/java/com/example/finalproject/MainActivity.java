package com.example.finalproject;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;

import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


import com.example.finalproject.adapters.NotesAdapter;
import com.example.finalproject.entities.DBHelper;
import com.example.finalproject.entities.Note;
import com.example.finalproject.listeners.NotesListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesListener {

        public static final int REQUEST_CODE_ADD_NOTE = 1;
        public static final int REQUEST_CODE_UPDATE = 2;
        public static final int REQUEST_CODE_SHOW_NOTES = 3;
        public static final int REQUEST_CODE_DELETE_SELECT = 4;
        DBHelper db;
        private RecyclerView notesRecyclerView;
        private List<Note> noteList;
        private NotesAdapter notesAdapter;
        private AlertDialog dialogDelete;
        private int noteClickedPosition = -1;
        private boolean isdelete = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DBHelper(this);


//        ActionBar actionBar;
//        actionBar = getSupportActionBar();
//        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#82E0AA"));
//        actionBar.setBackgroundDrawable(colorDrawable);

        ImageView imageAddNoteMain = findViewById(R.id.imageAddNotesMain);

        imageAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              startActivityForResult(
                      new Intent(getApplicationContext(),CreateNoteActivity.class),
                      REQUEST_CODE_ADD_NOTE
              );

            }
        });

        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        );

        noteList = new ArrayList<>();
        notesAdapter = new NotesAdapter(noteList,this,this);

        notesRecyclerView.setAdapter(notesAdapter);
        getNotes(REQUEST_CODE_SHOW_NOTES,false);
//        EditText inputSearch= (EditText) findViewById(R.id.inputSearch);
//        inputSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                   notesAdapter.CancelTimer();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                    if (noteList.size() != 0){
//                        notesAdapter.searchNotes(s.toString());
//                    }
//
//            }
//        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search,menu);
        MenuItem search = menu.findItem(R.id.searchKey);
        SearchView searchView = (SearchView) search.getActionView();
            searchView.setQueryHint("Tìm kiếm");
            searchView.setMaxWidth(Integer.MAX_VALUE);
            SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    if (noteList.size() > 0) {
                        notesAdapter.searchNotes(s);
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    if (noteList.size() > 0) {
                        notesAdapter.searchNotes(s);
                    }
                    return true;
                }
            };
            searchView.setOnQueryTextListener(listener);
        return true;
    }



    @Override
    public void onNoteClicked(Note note, int position) {

       noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(),CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate",true);
        intent.putExtra("note",note);
        startActivityForResult(intent,REQUEST_CODE_UPDATE);

    }

    @Override
    public void onNoteLongClicked(List<Note> notes) {

        if (notes.size() > 0){
            List<Note> list = new ArrayList<>();
            list.addAll(notes);
//
            showDeleteNoteDialog(list);
        }



    }

    private void showDeleteNoteDialog(List<Note> notes){
//        AlertDialog.Builder b = new AlertDialog.Builder(this);
//        b.setTitle("make select");
//        b.setMessage("are you right ?");
//        b.setPositiveButton("ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                for (Note note : notes){
//                        db.deleteNote(note.getId());
//                    }
//
//                    getNotes(REQUEST_CODE_DELETE_SELECT,false);
//            }
//        });
//        b.setNegativeButton("cacel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.cancel();
//            }
//        });
//        AlertDialog al = b.create();
//        al.show();


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_note,
                    (ViewGroup) findViewById(R.id.layoutDeleteNoteContainer)
            );
            builder.setView(view);

            dialogDelete = builder.create();
            if (dialogDelete.getWindow() != null){
                dialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            TextView delete = (TextView) view.findViewById(R.id.textDeleteNote);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (Note note : notes){
                        db.deleteNote(note.getId());
                    }
                        dialogDelete.hide();
                    getNotes(REQUEST_CODE_DELETE_SELECT,false);


                }
            });
            TextView cacel = (TextView) view.findViewById(R.id.textCancel);
            cacel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogDelete.dismiss();
                }
            });
        dialogDelete.show();

    }

    private void getNotes(final int requestCode,final boolean isNoteDeleted){

        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask<Void, Void, List<Note>>{
            @Override
            protected List<Note> doInBackground(Void... voids) {
                return db.getAllNote();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);

                if (requestCode == REQUEST_CODE_SHOW_NOTES){
                    noteList.clear();
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();

                }else if (requestCode == REQUEST_CODE_ADD_NOTE){
                    notes.add(0,notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                    notesRecyclerView.smoothScrollToPosition(0);
                }else if (requestCode == REQUEST_CODE_UPDATE){
                    noteList.remove(noteClickedPosition);
                    if (isNoteDeleted){

                        noteList.clear();
                        noteList.addAll(notes);
                        notesAdapter.notifyDataSetChanged();

                    }else{
                        noteList.add(noteClickedPosition,notes.get(noteClickedPosition));
                        notesAdapter.notifyItemChanged(noteClickedPosition);
                    }
                }else if (requestCode == REQUEST_CODE_DELETE_SELECT){
                    noteList.clear();
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                }

//               if(noteList.size() == 0){
//                   noteList.addAll(notes);
//                   notesAdapter.notifyDataSetChanged();
//               }else{
//                   notes.add(0,notes.get(0));
//                   notesAdapter.notifyItemInserted(0);
//               }
//               notesRecyclerView.smoothScrollToPosition(0);

            }
        }
        new GetNotesTask().execute();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK){
                getNotes(REQUEST_CODE_ADD_NOTE,false);
        }else if (requestCode == REQUEST_CODE_UPDATE && resultCode == RESULT_OK){
            if (data !=null){
                getNotes(REQUEST_CODE_UPDATE,data.getBooleanExtra("isNoteDeleted",false));
            }
        }
    }
}