package com.jerrwu.quintic.main.viewmodel;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.jerrwu.quintic.common.base.BaseViewModel;
import com.jerrwu.quintic.common.constants.ConstantLists;
import com.jerrwu.quintic.entities.cell.CellEntity;
import com.jerrwu.quintic.entities.time.DayEntity;
import com.jerrwu.quintic.entities.time.MonthEntity;
import com.jerrwu.quintic.entities.time.YearEntity;
import com.jerrwu.quintic.utils.CalDbHelper;
import com.jerrwu.quintic.utils.FileUtils;
import com.jerrwu.quintic.utils.GsonUtils;
import com.jerrwu.quintic.utils.SafetyUtils;
import com.jerrwu.quintic.utils.SearchUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class CalViewModel extends BaseViewModel {
    private static String TAG = CalViewModel.class.getSimpleName();

    private MutableLiveData<MonthEntity> mCurrentMonth = new MutableLiveData<>();
    private MutableLiveData<YearEntity> mCurrentYear = new MutableLiveData<>();
    private MutableLiveData<List<YearEntity>> mYears = new MutableLiveData<>();
    private MutableLiveData<List<CellEntity>> mCellList = new MutableLiveData<>();
    private MutableLiveData<List<CellEntity>> mWeekDayHeaders = new MutableLiveData<>();
    private boolean mAddSpacing = true;
    private int mCurrentMonthValue = 0;
    private int mCurrentYearValue = 0;
    private int mPreviousPosition = 0;

    public MutableLiveData<YearEntity> getCurrentYear() {
        if (mCurrentYear.getValue() == null) {
            mCurrentYear.setValue(new YearEntity(0, new ArrayList<>()));
        }
        Log.d(TAG, "getCurrentYear() called, returned " + mCurrentYear.getValue());
        return mCurrentYear;
    }

    public MutableLiveData<MonthEntity> getCurrentMonth() {
        if (mCurrentMonth.getValue() == null) {
            mCurrentMonth.setValue(new MonthEntity(0));
        }
        Log.d(TAG, "getCurrentMonth() called, returned " + mCurrentMonth.getValue());
        return mCurrentMonth;
    }

    public void setPreviousPosition(final int previousPosition) {
        mPreviousPosition = previousPosition;
    }

    public int getCurrentMonthValue() {
        return mCurrentMonthValue;
    }

    public int getCurrentYearValue() {
        return mCurrentYearValue;
    }

    public int getPreviousPosition() {
        return mPreviousPosition;
    }

    public void setCurrentMonth(final MonthEntity currentMonth) {
        mCurrentMonth.setValue(currentMonth);
        mCurrentMonthValue = currentMonth.getNumber();
    }

    public void setCurrentYear(final YearEntity currentYear) {
        mCurrentYear.setValue(currentYear);
        mCurrentYearValue = currentYear.getNumber();
    }

    private void loadWeekDayHeader() {
        Log.d(TAG, "loadWeekDayHeader() called");
        if (mWeekDayHeaders.getValue() == null) {
            final List<CellEntity> headers = new ArrayList<>();
            for (String header : ConstantLists.INSTANCE.getCalHeaders()) {
                headers.add(new CellEntity(header));
            }
            mWeekDayHeaders.setValue(headers);
        }
    }

    private void loadCells(final Context context) {
        loadWeekDayHeader();
        final List<CellEntity> cells = new ArrayList<>(SafetyUtils.orEmpty(mWeekDayHeaders.getValue()));
        for (DayEntity day : SafetyUtils.orEmpty(
                SafetyUtils.nonNullMonth(mCurrentMonth.getValue()).getDays())) {
            if (mAddSpacing) {
                mAddSpacing = false;
                for (int ignored : IntStream.range(1, day.getDayOfWeek()).toArray()) {
                    cells.add(new CellEntity(""));
                }
            }
            final String searchDate = String.valueOf(mCurrentYearValue) + mCurrentMonthValue + day.getDayOfMonth();
            final CalDbHelper calDbHelper = new CalDbHelper(context);
            final List<Integer> result = SearchUtils.INSTANCE.
                    performCalEntryCountSearch(searchDate, calDbHelper);
            calDbHelper.close();
            cells.add(new CellEntity(String.valueOf(day.getDayOfMonth()), result.get(1)));
        }
        Log.d(TAG, "loadCells() called, loaded " + cells);
        mCellList.setValue(cells);
    }

    public MutableLiveData<List<CellEntity>> getCellList(final Context context) {
        loadCells(context);
        Log.d(TAG, "getCellList() called, returned " + mCellList.getValue());
        return mCellList;
    }

    private void loadYears(final Context context) {
        List<YearEntity> years = new Gson().fromJson(
                FileUtils.INSTANCE.fromAssetsJson(context, "calendar.json"),
                GsonUtils.INSTANCE.getYearListType());
        Log.d(TAG, "loadYears() called, loaded " + years);
        mYears.setValue(years);
    }

    public MutableLiveData<List<YearEntity>> getYears(final Context context) {
        if (mYears.getValue() == null) {
            loadYears(context);
        }
        Log.d(TAG, "getYears() called, returned " + mYears.getValue());
        return mYears;
    }
}
