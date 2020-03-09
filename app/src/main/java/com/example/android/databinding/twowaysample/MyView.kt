package com.example.android.databinding.twowaysample

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.TextView

/**
 * Created by dumingwei on 2020-03-08.
 * Desc:
 */
class MyView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {

    var time: String? = null
        set(value) {
            field = value
            //time改变了要改变text
            MyView@ this.setText(value)
        }

    init {

        val ta: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.MyView)

        time = ta.getString(R.styleable.MyView_time)

        MyView@ this.setText(time)

        ta.recycle()

    }
}