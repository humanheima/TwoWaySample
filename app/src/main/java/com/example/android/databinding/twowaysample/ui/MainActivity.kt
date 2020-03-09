package com.example.android.databinding.twowaysample.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.android.databinding.twowaysample.BlogDemoActivity
import com.example.android.databinding.twowaysample.R
import com.example.android.databinding.twowaysample.databinding.ActivityMainBinding

/**
 * Crete by dumingwei on 2020-03-09
 * Desc:
 *
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.btnIntervalTimer.setOnClickListener {
            IntervalTimerActivity.launch(this)
        }
        binding.btnBlogDemo.setOnClickListener {
            BlogDemoActivity.launch(this)
        }
    }
}
