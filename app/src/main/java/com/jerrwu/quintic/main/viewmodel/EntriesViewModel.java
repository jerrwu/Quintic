package com.jerrwu.quintic.main.viewmodel;

import android.app.Activity;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.preference.PreferenceManager;

import com.jerrwu.quintic.common.base.BaseViewModel;
import com.jerrwu.quintic.entities.entry.EntryEntity;
import com.jerrwu.quintic.utils.CalDbHelper;
import com.jerrwu.quintic.utils.MainDbHelper;

import java.util.ArrayList;
import java.util.List;

public class EntriesViewModel extends BaseViewModel {
    public static String TAG = EntriesViewModel.class.getSimpleName();

    private MutableLiveData<List<EntryEntity>> mEntryList = new MutableLiveData<>();
    private CalDbHelper mCalDbHelper;
    private MainDbHelper mMainDbHelper;

    public MutableLiveData<List<EntryEntity>> getEntryList() {

        return mEntryList;
    }

}
