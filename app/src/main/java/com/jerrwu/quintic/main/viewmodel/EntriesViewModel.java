package com.jerrwu.quintic.main.viewmodel;

import android.app.Activity;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.preference.PreferenceManager;

import com.jerrwu.quintic.entities.entry.EntryEntity;
import com.jerrwu.quintic.utils.CalDbHelper;
import com.jerrwu.quintic.utils.MainDbHelper;

import java.util.ArrayList;
import java.util.List;

public class EntriesViewModel extends ViewModel {
    public static String TAG = EntriesViewModel.class.getSimpleName();

    private MutableLiveData<List<EntryEntity>> mEntryList = new MutableLiveData<>();
    private MutableLiveData<SharedPreferences> mPreferences = new MutableLiveData<>();
    private CalDbHelper mCalDbHelper;
    private MainDbHelper mMainDbHelper;

    public MutableLiveData<SharedPreferences> getPreferences(Activity activity) {
        if (mPreferences.getValue() == null) {
            mPreferences.postValue(PreferenceManager.getDefaultSharedPreferences(activity));
        }
        return mPreferences;
    }

    public MutableLiveData<List<EntryEntity>> getEntryList() {

        return mEntryList;
    }

}
