package app.joze.stopwatch.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import app.joze.stopwatch.util.Constants
import app.joze.stopwatch.util.formatTime
import app.joze.stopwatch.util.pad
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
@ExperimentalAnimationApi
@AndroidEntryPoint
class StopwatchService : Service() {

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    private val binder = StopwatchBinder()

    private var duration: Duration = Duration.ZERO
    private lateinit var timer: Timer


    var seconds = mutableStateOf("00")
        private set
    var minutes = mutableStateOf("00")
        private set
    var hours = mutableStateOf("00")
        private set

    var currentState = mutableStateOf(StopwatchState.Idle)
        private set


    override fun onBind(intent: Intent?) = binder

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.getStringExtra(Constants.STOPWATCH_STATE)) {
            StopwatchState.Started.name -> {
                setPauseButton()
                startForegroundService()
                startStopwatch(onTick = {
                    h, m, s ->
                    updateNotification(h, m, s)
                })
            }

            StopwatchState.Paused.name -> {
                pauseStopwatch()
                setResumeButton()
            }

            StopwatchState.Stopped.name -> {
                pauseStopwatch()
                stopStopwatch()
                stopForegroundService()
            }
        }

        intent?.action.let {
            when(it){
                Constants.ACTION_SERVICE_START->{
                    setPauseButton()
                    startForegroundService()
                    startStopwatch(onTick = {
                        h, m, s ->
                        updateNotification(h, m, s)
                    })
                }
                Constants.ACTION_SERVICE_PAUSE->{
                    pauseStopwatch()
                    setResumeButton()
                }
                Constants.ACTION_SERVICE_STOP->{
                    pauseStopwatch()
                    stopStopwatch()
                    stopForegroundService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }


    //_____________________________________________________________________

    private fun startStopwatch(onTick: (h: String, m: String, s: String) -> Unit) {
        Log.d("MainActivity", "hi this is $currentState")
        currentState.value = StopwatchState.Started
        timer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
            duration = duration.plus(1.seconds)
            updateTimeUnits()
            onTick(hours.value, minutes.value, seconds.value)
        }
        Log.d("MainActivity", "hi this is ${currentState.value}")

    }

    private fun pauseStopwatch() {
        Log.d("MainActivity", "hi stop is $currentState")

        if (this::timer.isInitialized) {
            timer.cancel()
        }

        currentState.value = StopwatchState.Paused
    }

    private fun stopStopwatch() {
        duration = Duration.ZERO
        currentState.value = StopwatchState.Idle
        updateTimeUnits()
    }

    //_____________________________________________________________________

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                Constants.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun updateNotification(hours: String, minute: String, seconds: String) {
        notificationManager.notify(
            Constants.NOTIFICATION_ID,
            notificationBuilder.setContentText(
                formatTime(
                    hours = hours,
                    minutes = minute,
                    seconds = seconds
                )
            ).build()
        )
    }

    //_____________________________________________________________________
    @SuppressLint("RestrictedApi")
    private fun setPauseButton() {
        notificationBuilder.mActions.removeAt(0)

        notificationBuilder.mActions.add(
            0,
            NotificationCompat.Action(
                0,
                "Pause",
                ServiceHelper.pausePendingIntent(this)
            )
        )

        notificationManager.notify(Constants.NOTIFICATION_ID, notificationBuilder.build())
    }

    @SuppressLint("RestrictedApi")
    private fun setResumeButton() {
        notificationBuilder.mActions.removeAt(0)
        notificationBuilder.mActions.add(
            0,
            NotificationCompat.Action(
                0,
                "Resume",
                ServiceHelper.resumePendingIntent(this)
            )
        )

        notificationManager.notify(Constants.NOTIFICATION_ID, notificationBuilder.build())
    }
    //_____________________________________________________________________

    @SuppressLint("ForegroundServiceType")
    private fun startForegroundService(){
        createNotificationChannel()
        startForeground(Constants.NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun stopForegroundService(){
        notificationManager.cancel(Constants.NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
    //_____________________________________________________________________

    private fun updateTimeUnits() {
        duration.toComponents { hours, minutes, seconds, _ ->
            this@StopwatchService.hours.value = hours.toInt().pad()
            this@StopwatchService.minutes.value = minutes.pad()
            this@StopwatchService.seconds.value = seconds.pad()
        }
    }

    inner class StopwatchBinder : Binder() {
        fun getService(): StopwatchService = this@StopwatchService
    }
}

enum class StopwatchState {
    Idle,
    Started,
    Paused,
    Stopped
}