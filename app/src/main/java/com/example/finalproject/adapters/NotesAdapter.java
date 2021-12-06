package com.example.finalproject.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.MainViewModel;
import com.example.finalproject.R;
import com.example.finalproject.entities.DBHelper;
import com.example.finalproject.entities.Note;
import com.example.finalproject.listeners.NotesListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

//ItemNotes
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder>{

    private List<Note> notes;
    private NotesListener notesListener;

    private Timer timer;
    private List<Note> noteSource;
    DBHelper db;
    private boolean isEnable = false;
    private boolean isSelectAll = false;
    private List<Note> selectList = new ArrayList<>();
    private MainViewModel mainViewModel;
    private Activity activity;


    public NotesAdapter(List<Note> notes,NotesListener notesListener,Activity activity)
    {
        this.notes = notes;
        this.notesListener=notesListener;
        noteSource=notes;
        this.activity = activity;
    }
    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mainViewModel = new ViewModelProvider((FragmentActivity) activity).get(MainViewModel.class);
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_note,
                        parent,
                        false
                )
        );
    }
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setNote(notes.get(position));
        holder.layoutNote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!isEnable){
                    ActionMode.Callback callback = new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                            MenuInflater menuInflater = actionMode.getMenuInflater();
                            menuInflater.inflate(R.menu.mul_del,menu);
                            return true;
                        }
                        @Override
                        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                            isEnable = true;
                            ClickItem(holder);
                            mainViewModel.getValue().observe((LifecycleOwner) activity
                                    , new Observer<String>() {
                                        @Override
                                        public void onChanged(String s) {
                                                actionMode.setTitle(String.format("%s Đã chọn",s));
                                        }
                                    });

                            return true;
                        }
                        @Override
                        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

                                switch (menuItem.getItemId()){
                                    case R.id.menu_delete:
                                        notesListener.onNoteLongClicked(selectList);
                                        for (Note note : noteSource){
                                            selectList.remove(note);
                                        }
                                        actionMode.finish();
                                        break;
                                    case R.id.menu_selectAll:
                                            if (selectList.size() == noteSource.size()){
                                                isSelectAll = false;
                                                selectList.clear();
                                            }else{
                                                isSelectAll = true;
                                                selectList.clear();
                                                selectList.addAll(notes);
                                            }
                                            mainViewModel.setValue(String.valueOf(selectList.size()));
                                            notifyDataSetChanged();
                                        break;
                                }
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode actionMode) {
                                isEnable = false;
                                isSelectAll = false;
                                selectList.clear();
                                notifyDataSetChanged();
                        }
                    };
                    ((AppCompatActivity) view.getContext()).startActionMode(callback);
                }else{
                    ClickItem(holder);
                }
                return true;
            }
        });
        holder.layoutNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEnable){
                    ClickItem(holder);
                }else{
                    notesListener.onNoteClicked(notes.get(position),position);
                }
            }
        });
       if (isSelectAll){
           holder.checkDelete.setVisibility(View.VISIBLE);

       }else{
           holder.checkDelete.setVisibility(View.GONE);
       }
    }

    private void ClickItem(NoteViewHolder holder) {
        Note note = noteSource.get(holder.getAdapterPosition());
        if (holder.checkDelete.getVisibility() == View.GONE){
            holder.checkDelete.setVisibility(View.VISIBLE);

            selectList.add(note);
        }else{
            holder.checkDelete.setVisibility(View.GONE);

            selectList.remove(note);
        }
        mainViewModel.setValue(String.valueOf(selectList.size()));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView textTitle, textSubTitle, textDateTime,textNoteContent;
        LinearLayout layoutNote;
        RoundedImageView imageNote;
        ImageView checkDelete;
         NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textSubTitle = itemView.findViewById(R.id.textSubTitle);
             textNoteContent = itemView.findViewById(R.id.textNoteContent);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            layoutNote = itemView.findViewById(R.id.layoutNote);
            imageNote = itemView.findViewById(R.id.imageNote);
            checkDelete = itemView.findViewById(R.id.checkDelete);

         }
        void setNote(Note note){
             //fill data to itemNote

            if (note.getTitle().trim().isEmpty()){
                textTitle.setVisibility(View.GONE);
            }else{
                textTitle.setText(note.getTitle());
                textTitle.setVisibility(View.VISIBLE);
            }
            textTitle.setText(note.getTitle());
             if (note.getSubtitle().trim().isEmpty()){
                 textSubTitle.setVisibility(View.GONE);
             }else{
                 textSubTitle.setText(note.getSubtitle());
                 textSubTitle.setVisibility(View.VISIBLE);
             }

            textNoteContent.setText(note.getNoteText());
             textDateTime.setText(
                     note.getDatetime()
             );

             //set color for item note
            GradientDrawable gradientDrawable = (GradientDrawable) layoutNote.getBackground();
            if (note.getColor() != null){
                gradientDrawable.setColor(Color.parseColor(note.getColor()));
            }else{
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }
            if (note.getImage() != null){
                imageNote.setImageBitmap(BitmapFactory.decodeFile(note.getImage()));
                imageNote.setVisibility(View.VISIBLE);
            }else{

                imageNote.setVisibility(View.GONE);
            }
        }
    }
    public void searchNotes(final String searchKeyWord){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyWord.trim().isEmpty()){
                    notes=noteSource;
                }else{
                    ArrayList<Note> temp = new ArrayList<>();
                    for (Note note: noteSource) {
                        if (note.getTitle().toLowerCase().contains(searchKeyWord.toLowerCase())
                                || note.getSubtitle().toLowerCase().contains(searchKeyWord.toLowerCase())
                                || note.getNoteText().toLowerCase().contains(searchKeyWord.toLowerCase())){
                            temp.add(note);
                        }
                    }
                    notes = temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        },500);
    }
    public void CancelTimer(){
        if (timer != null){
            timer.cancel();
        }
    }
}
