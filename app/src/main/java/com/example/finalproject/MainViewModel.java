package com.example.finalproject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.finalproject.entities.Note;

public class MainViewModel extends ViewModel {
    MutableLiveData<String> mutableLiveData = new MutableLiveData<>();

    public void setValue(String s){
        mutableLiveData.setValue(s);
    }
    public MutableLiveData<String> getValue(){
        return mutableLiveData;
    }
}
