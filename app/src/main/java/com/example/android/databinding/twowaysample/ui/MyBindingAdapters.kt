package com.example.android.databinding.twowaysample.ui

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.TextView
import androidx.databinding.*
import androidx.databinding.adapters.ListenerUtil
import com.example.android.databinding.twowaysample.MyView

/**
 * Created by dumingwei on 2020-03-08.
 * Desc:
 */
/*@InverseBindingMethods(
        InverseBindingMethod(
                type = MyView::class,
                attribute = "time",
                event = "timeAttrChanged",
                method = "getTime"
        )
)*/
object MyBindingAdapters {


    private val TAG = "MyBindingAdapters"

    @BindingAdapter("time")
    @JvmStatic
    fun setTime(view: MyView, newValue: String) {
        // Important to break potential infinite loops.
        val oldValue = view.time
        if (oldValue != newValue) {
            Log.d(TAG, "setTime: oldValue = $oldValue , newValue = $newValue")
            view.time = newValue
        }
    }

    @InverseBindingAdapter(attribute = "time", event = "timeAttrChanged")
    @JvmStatic
    fun getTime(view: MyView): String? {
        val time = view.time
        Log.d(TAG, "getTime: time = " + time)
        return time
    }

    @BindingAdapter("timeAttrChanged")
    @JvmStatic
    fun setListeners(view: MyView, attrChange: InverseBindingListener?) {
        Log.d(TAG, "setListeners: ")
        val newValue: TextWatcher?
        if (attrChange == null) {
            newValue = null
        } else {
            newValue = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                    //do nothing
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    attrChange.onChange()
                }

                override fun afterTextChanged(s: Editable) {
                    //do nothing
                }
            }
        }
        val oldValue = ListenerUtil.trackListener(view, newValue, androidx.databinding.library.baseAdapters.R.id.textWatcher)
        if (oldValue != null) {
            view.removeTextChangedListener(oldValue)
        }
        if (newValue != null) {
            view.addTextChangedListener(newValue)
        }
    }

}