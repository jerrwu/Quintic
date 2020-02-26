package com.jerrwu.quintic.common.base

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.jerrwu.quintic.common.EditTextFlow
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

abstract class BaseActivity : AppCompatActivity() {
    companion object {
        val TAG = BaseActivity::class.java.simpleName
    }

    lateinit var mViewModel: ViewModel

    fun EditText.addTextWatcher(): Flowable<EditTextFlow> {
        return Flowable.create<EditTextFlow>({ emitter ->
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    emitter.onNext(
                        EditTextFlow(
                            p0.toString(),
                            EditTextFlow.Type.BEFORE
                        )
                    )
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    emitter.onNext(
                        EditTextFlow(
                            p0.toString(),
                            EditTextFlow.Type.ON
                        )
                    )
                }

                override fun afterTextChanged(p0: Editable?) {
                    emitter.onNext(
                        EditTextFlow(
                            p0.toString(),
                            EditTextFlow.Type.AFTER
                        )
                    )
                }
            })
        }, BackpressureStrategy.BUFFER)
    }

    protected fun utilsFinish(context: Context) {
        finish()
    }

}