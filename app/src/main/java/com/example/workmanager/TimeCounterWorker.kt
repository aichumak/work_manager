package com.example.workmanager

import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.Looper
import android.util.Log
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

class TimeCounterWorker(
    private val context: Context,
    private val workerParameters: WorkerParameters
) :
    Worker(context, workerParameters) {

    override fun doWork(): Result {
        val minutesCount = workerParameters.inputData.getInt(MINUTES_COUNT_KEY, 0).toLong()
        Looper.prepare()
        val countDownTimer = object : CountDownTimer(minutesCount * 60000-1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d("TestWorker", "${this@TimeCounterWorker.id}      $millisUntilFinished")
                val intent = Intent(WORK_NAME)
                intent.putExtra(MILLIS_COUNT_KEY, millisUntilFinished)
                context.sendBroadcast(intent)
                val logText = getTimeStringFromDouble(millisUntilFinished)
                Log.d("TestWorker", "++++++++      $logText")

//                return try {
//                    val outputData =
//                        createOutputData(millisUntilFinished / 1000, millisUntilFinished % 1000)
//                    Result.success(outputData)
//                } catch (e: Exception) {
//                    val outputData = createOutputData(-1, -1)
//                    Result.failure(outputData)
//                }
            }

            override fun onFinish() {
                Log.d("TestWorker", "${this@TimeCounterWorker.id} finished")
            }
        }.start()
        Looper.loop()
        return Result.success()
    }

//    private fun createOutputData(minutes: Int, seconds: Int): Data {
//        return Data.Builder()
//            .putInt(MINUTES_COUNT_KEY, minutes)
//            .putInt(SECONDS_COUNT_KEY, seconds)
//            .build()
//    }

    private fun getTimeStringFromDouble(millis: Long): String {
        //val hours = (millis % 86400 / 3600).toDouble().roundToInt()
        val minutes = (millis / 60000).toInt()
        val seconds = (millis / 1000 % 60).toInt()

        return makeTimeString(minutes, seconds)
    }

    private fun makeTimeString(min: Int, sec: Int) = "$min : $sec"

    companion object {

        const val MINUTES_COUNT_KEY = "MINUTES_COUNT_KEY"
        const val MILLIS_COUNT_KEY = "millis"
        const val WORK_NAME = "TimeCounterWorker"

        fun makeRequest(minutes: Int): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<TimeCounterWorker>().apply {
                setInputData(workDataOf(MINUTES_COUNT_KEY to minutes))
                setConstraints(makeConstraints())
            }.build()
        }

        private fun makeConstraints() = Constraints.Builder()
            //.setRequiresCharging(true)
            .build()
    }
}