package app.joze.stopwatch.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.animation.ExperimentalAnimationApi
import app.joze.stopwatch.MainActivity
import app.joze.stopwatch.util.Constants
import app.joze.stopwatch.util.Constants.PAUSE_REQUEST_CODE
import app.joze.stopwatch.util.Constants.STOPWATCH_STATE

@OptIn(ExperimentalAnimationApi::class)
object ServiceHelper {

    private val flag =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            PendingIntent.FLAG_IMMUTABLE
        else
            0

    fun clickPendingIntent(context: Context): PendingIntent {
        val clickIntent = Intent(context, MainActivity::class.java).apply {
            putExtra(STOPWATCH_STATE, StopwatchState.Started.name)
        }

        return PendingIntent.getActivity(
            context, Constants.CLICK_REQUEST_CODE, clickIntent, flag
        )
    }

    fun pausePendingIntent(context: Context): PendingIntent {
        val stopIntent = Intent(context, StopwatchService::class.java).apply {
            putExtra(STOPWATCH_STATE, StopwatchState.Paused.name)
        }
        return PendingIntent.getService(
            context, PAUSE_REQUEST_CODE, stopIntent, flag
        )
    }

    fun stopPendingIntent(context: Context): PendingIntent {
        val stopIntent = Intent(
            context, StopwatchService::class.java
        ).apply {
            putExtra(STOPWATCH_STATE, StopwatchState.Stopped.name)
        }

        return PendingIntent.getService(
            context, Constants.STOP_REQUEST_CODE, stopIntent, flag
        )
    }

    fun resumePendingIntent(context: Context): PendingIntent {
        val resumeIntent = Intent(context, StopwatchService::class.java).apply {
            putExtra(STOPWATCH_STATE, StopwatchState.Started.name)
        }

        return PendingIntent.getService(
            context, Constants.RESUME_REQUEST_CODE, resumeIntent, flag
        )
    }


    fun triggerForegroundService(context: Context, action: String) {
        Intent(context, StopwatchService::class.java).apply {
            this.action = action
            context.startService(this)
        }
    }
}