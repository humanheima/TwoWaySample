package com.example.android.databinding.twowaysample.data

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel

/**
 * Created by dumingwei on 2020-03-08.
 * Desc:
 */
class MyDataViewModel : ViewModel() {

    val name: ObservableField<String> = ObservableField()

    val time: ObservableField<String> = ObservableField()

    val birthDate: ObservableField<Long> = ObservableField()

    fun changeName() {
        name.set(name.get() + "_dmw")
    }

    fun changeTime() {
        time.set(time.get() + "时光匆匆")
    }

}