package com.jerrwu.quintic.search;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class RxSearchObservable {

    public static Observable<String> fromEditText(EditText searchField) {

        final PublishSubject<String> subject = PublishSubject.create();

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                subject.onNext(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                subject.onComplete();
            }
        });

        return subject;
    }
}
