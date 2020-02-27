package com.jerrwu.quintic.common.base;

import android.app.Activity;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.preference.PreferenceManager;

abstract public class BaseViewModel extends ViewModel {
    private MutableLiveData<SharedPreferences> mPreferences = new MutableLiveData<>();

    public MutableLiveData<SharedPreferences> getPreferences(Activity activity) {
        if (mPreferences.getValue() == null) {
            mPreferences.postValue(PreferenceManager.getDefaultSharedPreferences(activity));
        }
        return mPreferences;
    }
}
