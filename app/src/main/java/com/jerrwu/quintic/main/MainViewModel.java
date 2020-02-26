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
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends ViewModel {
    private static String TAG = MainViewModel.class.getSimpleName();

    private MutableLiveData<List<BaseFragment>> mNavigationFragments;
    private MutableLiveData<BaseFragment> mActiveFragment;

    private void loadNavigationFragments() {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                if (mNavigationFragments == null) {
                    List<BaseFragment> navigationFragments = new ArrayList<>();
                    navigationFragments.add(new EntriesFragment());
                    navigationFragments.add(new SearchFragment());
                    navigationFragments.add(new CalFragment());

                    mNavigationFragments.setValue(navigationFragments);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "on cleared called");
    }
}
