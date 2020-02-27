package com.jerrwu.quintic.main.viewmodel;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jerrwu.quintic.common.base.BaseViewModel;
import com.jerrwu.quintic.entities.entry.EntryEntity;
import com.jerrwu.quintic.entities.mood.MoodEntity;
import com.jerrwu.quintic.utils.CalDbHelper;
import com.jerrwu.quintic.utils.MainDbHelper;
import com.jerrwu.quintic.utils.SearchUtils;

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

    public void verifyMainDb(final Context context) {
        if (mMainDbHelper == null) {
            mMainDbHelper = new MainDbHelper(context);
        }
    }

    public void verifyCalDb(final Context context) {
        if (mCalDbHelper == null) {
            mCalDbHelper = new CalDbHelper(context);
        }
    }

    public MutableLiveData<List<EntryEntity>> getFreshEntryList() {
        doRefresh();
        return getEntryList();
    }

    public MutableLiveData<List<EntryEntity>> getEntryList() {
        if (mEntryList.getValue() == null) {
            doRefresh();
        }
        return mEntryList;
    }

    public void deleteEntries(final List<EntryEntity> entries) {
        if (mMainDbHelper != null && mCalDbHelper != null) {
            for (final EntryEntity entry : entries) {
                final LocalDateTime createdDate = entry.getTime();
                final String calDbDate = createdDate.getYear() +
                        String.valueOf(createdDate.getMonthValue()) +
                        createdDate.getDayOfMonth();

                final List<Integer> result = SearchUtils.INSTANCE.performCalEntryCountSearch(calDbDate, mCalDbHelper);
                final Integer calId = result.get(0);
                final Integer entryCount = result.get(1);

                final ContentValues columnValues = new ContentValues();
                columnValues.put(CalDbHelper.DB_COL_DATE, Integer.valueOf(calDbDate));
                columnValues.put(CalDbHelper.DB_COL_ENTRIES, entryCount - 1);

                String[] selectionArgs = { calId.toString() };
                mCalDbHelper.update(columnValues, "ID=?", selectionArgs);

                selectionArgs = new String[] { String.valueOf(entry.getId()) };
                mMainDbHelper.delete("ID=?", selectionArgs);
            }
        }
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
