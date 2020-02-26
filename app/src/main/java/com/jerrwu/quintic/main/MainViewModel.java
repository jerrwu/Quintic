package com.jerrwu.quintic.main;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jerrwu.quintic.common.base.BaseFragment;
import com.jerrwu.quintic.main.fragment.CalFragment;
import com.jerrwu.quintic.main.fragment.EntriesFragment;
import com.jerrwu.quintic.main.fragment.SearchFragment;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends ViewModel {
    private static String TAG = MainViewModel.class.getSimpleName();

    private MutableLiveData<List<BaseFragment>> mNavigationFragments;
    private MutableLiveData<Integer> mActiveIndex;

    private void loadNavigationFragments() {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                List<BaseFragment> navigationFragments = new ArrayList<>();
                navigationFragments.add(new EntriesFragment());
                navigationFragments.add(new SearchFragment());
                navigationFragments.add(new CalFragment());

                mNavigationFragments.postValue(navigationFragments);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public MutableLiveData<Integer> getActiveFragment() {
        if (mActiveIndex == null) {
            mActiveIndex = new MutableLiveData<>();
            mActiveIndex.postValue(0);
        }
        return mActiveIndex;
    }

    public MutableLiveData<List<BaseFragment>> getNavigationFragments() {
        if (mNavigationFragments == null) {
            mNavigationFragments = new MutableLiveData<>();
            loadNavigationFragments();
        }
        return mNavigationFragments;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "on cleared called");
    }
}
