package com.example.android.databinding.twowaysample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.android.databinding.twowaysample.data.MyDataViewModel
import com.example.android.databinding.twowaysample.databinding.ActivityBlogDemoBinding
import com.example.android.databinding.twowaysample.util.MyConverter
import kotlinx.android.synthetic.main.activity_blog_demo.*

/**
 * Crete by dumingwei on 2020-03-08
 * Desc:
 *
 */
class BlogDemoActivity : AppCompatActivity() {

    companion object {

        fun launch(context: Context) {
            val intent = Intent(context, BlogDemoActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val TAG = "BlogDemoActivity"

    lateinit var viewModel: MyDataViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(MyDataViewModel::class.java)
        viewModel.name.set("Ada")
        viewModel.time.set("已经是晚上11点了")
        viewModel.birthDate.set(System.currentTimeMillis())

        val binding: ActivityBlogDemoBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_blog_demo)
        binding.viewModel = viewModel
        /*MyBindingAdapters.setListeners(binding.myView, object : InverseBindingListener {
            override fun onChange() {
                val newTimeString = MyBindingAdapters.getTime(binding.myView)
                if (viewModel != null) {
                    viewModel.time.set(newTimeString)
                }

            }
        })*/
        binding.lifecycleOwner = this
        binding.btnChangeText.setOnClickListener {
            binding.etText.setText("Hello world")

            Log.d(TAG, "onCreate: binding.etText.text = " + binding.etText.text)
            Log.d(TAG, "onCreate: viewModel.name = " + viewModel.name.get())
        }

        btnChangeViewModelTime.setOnClickListener {
            viewModel.changeTime()
            it.postDelayed({
                Log.d(TAG, "onCreate: binding.myView.text = " + binding.myView.time)
                Log.d(TAG, "onCreate: viewModel.time = " + viewModel.time.get())
            }, 1000)
        }
        binding.btnChangeMyViewTime.setOnClickListener {
            binding.myView.time = "时光匆匆如流水"

            Log.d(TAG, "onCreate: binding.myView.text = " + binding.myView.time)
            Log.d(TAG, "onCreate: viewModel.time = " + viewModel.time.get())
        }
        binding.btnChangeViewModelDate.setOnClickListener {
            binding.etConvertText.text = "2020-03-02"

            val birthDate = viewModel.birthDate.get() ?: 0
            Log.d(TAG, "onCreate: viewModel.birthDate = $birthDate")

            Log.d(TAG, "onCreate: ${MyConverter.dateToString(birthDate)}")
        }

    }
}
