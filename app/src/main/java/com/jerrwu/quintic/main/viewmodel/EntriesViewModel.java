package com.jerrwu.quintic.main.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jerrwu.quintic.common.base.BaseViewModel;
import com.jerrwu.quintic.entities.entry.EntryEntity;
import com.jerrwu.quintic.entities.mood.MoodEntity;
import com.jerrwu.quintic.utils.CalDbHelper;
import com.jerrwu.quintic.utils.MainDbHelper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EntriesViewModel extends BaseViewModel {
    public static String TAG = EntriesViewModel.class.getSimpleName();

    private MutableLiveData<List<EntryEntity>> mEntryList = new MutableLiveData<>();
    private CalDbHelper mCalDbHelper;
    private MainDbHelper mMainDbHelper;

    public void verifyMainDb(Context context) {
        if (mMainDbHelper == null) {
            mMainDbHelper = new MainDbHelper(context);
        }
    }

    public void verifyCalDb(Context context) {
        if (mCalDbHelper == null) {
            mCalDbHelper = new CalDbHelper(context);
        }
    }

    public MutableLiveData<List<EntryEntity>> getEntryList() {
        if (mEntryList.getValue() == null) {
            doRefresh();
        }
        return mEntryList;
    }

    private void doRefresh() {
        if (mMainDbHelper != null) {
            final String[] projections = {
                    MainDbHelper.DB_COL_ID,
                    MainDbHelper.DB_COL_ICON,
                    MainDbHelper.DB_COL_TITLE,
                    MainDbHelper.DB_COL_CONTENT,
                    MainDbHelper.DB_COL_TIME,
                    MainDbHelper.DB_COL_MOOD,
                    MainDbHelper.DB_COL_TAGS
            };
            final String[] selectionArgs = {"%"};
            final Cursor cursor = mMainDbHelper.query(
                    projections, "Title like ?", selectionArgs, MainDbHelper.DB_COL_ID + " DESC");

            ArrayList<EntryEntity> entryList = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    final int entryId = cursor.getInt(cursor.getColumnIndex(MainDbHelper.DB_COL_ID));
                    final int entryIc = cursor.getInt(cursor.getColumnIndex(MainDbHelper.DB_COL_ICON));
                    final String entryTitle = cursor.getString(cursor.getColumnIndex(MainDbHelper.DB_COL_TITLE));
                    final String entryContent = cursor.getString(cursor.getColumnIndex(MainDbHelper.DB_COL_CONTENT));
                    final String entryTime = cursor.getString(cursor.getColumnIndex(MainDbHelper.DB_COL_TIME));
                    final String entryMood = cursor.getString(cursor.getColumnIndex(MainDbHelper.DB_COL_MOOD));
                    final String entryTags = cursor.getString(cursor.getColumnIndex(MainDbHelper.DB_COL_TAGS));

                    entryList.add(
                            new EntryEntity(entryId,
                                    entryIc,
                                    entryTitle,
                                    entryContent,
                                    LocalDateTime.parse(entryTime),
                                    MoodEntity.Companion.parse(entryMood),
                                    entryTags
                            )
                    );
                } while (cursor.moveToNext());
            }
            cursor.close();
            mEntryList.postValue(entryList);
        }
    }
}
