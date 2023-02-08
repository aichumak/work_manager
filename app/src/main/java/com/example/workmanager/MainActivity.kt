package com.example.workmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.example.workmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = checkNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerReceiver(updateTime, IntentFilter(TimeCounterWorker.WORK_NAME))
        binding.startWorker.setOnClickListener {
            val workManager = WorkManager.getInstance(applicationContext)
            workManager.enqueueUniqueWork(
                TimeCounterWorker.WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                TimeCounterWorker.makeRequest(5)
            )
        }

    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val time = intent.getLongExtra(TimeCounterWorker.MILLIS_COUNT_KEY, 0)
            binding.textView.text = getTimeStringFromDouble(time)
            Log.d("TestWorker", "--------  ${getTimeStringFromDouble(time)} ")
        }
    }

    private fun getTimeStringFromDouble(millis: Long): String {
        //val hours = (millis % 86400 / 3600).toDouble().roundToInt()
        val minutes = (millis / 60000).toInt()
        val seconds = (millis / 1000 % 60).toInt()

        return makeTimeString(minutes, seconds)
    }

    private fun makeTimeString(min: Int, sec: Int) = "$min : $sec"

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}