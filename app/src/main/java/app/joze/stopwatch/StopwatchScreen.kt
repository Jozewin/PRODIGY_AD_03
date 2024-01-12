package app.joze.stopwatch

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.joze.stopwatch.service.ServiceHelper
import app.joze.stopwatch.service.StopwatchService
import app.joze.stopwatch.service.StopwatchState
import app.joze.stopwatch.util.Constants

@Composable
fun StopwatchScreen(
    stopwatchService: StopwatchService
) {

    val context = LocalContext.current
    val hours by stopwatchService.hours
    val minutes by stopwatchService.minutes
    val seconds by stopwatchService.seconds

    val currentState by stopwatchService.currentState

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF333333))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        CutCornerShape(
                            topStart = 46.dp,
                            bottomEnd = 46.dp
                        )
                    )
                    .size(250.dp)
                    .background(color = Color(0xFFD9D9D9))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color(0xFFA8A8A8),
                                shape = RoundedCornerShape(26.dp)
                            )
                            .padding(16.dp),

                        ) {

                        Row {

                            Text(
                                text = hours,
                                fontSize = 64.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Text(
                                text = ":",
                                fontSize = 64.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = minutes,
                                fontSize = 64.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = ":",
                                fontSize = 64.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = seconds,
                                fontSize = 64.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                Log.d("MainActivity", "hi this is $currentState")

                                ServiceHelper.triggerForegroundService(
                                    context = context,
                                    action = if (currentState == StopwatchState.Started) Constants.ACTION_SERVICE_PAUSE
                                    else Constants.ACTION_SERVICE_START
                                )
                                Log.d("MainActivity", "hi this is $currentState")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentState == StopwatchState.Started) Color.Red
                                else Color(0xFF2A2929)
                            )

                        ) {
                            Text(
                                text = if (currentState == StopwatchState.Started) "Pause"
                                else if (currentState == StopwatchState.Paused) "Resume"
                                else "Start",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Light,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(30.dp))
                        Button(
                            onClick = {
                                ServiceHelper.triggerForegroundService(
                                    context = context,
                                    action = Constants.ACTION_SERVICE_STOP
                                )
                            },
                            enabled = seconds != "00" && currentState != StopwatchState.Started,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2A2929),
                                disabledContainerColor = Color(0x63040000)
                            )
                        ) {
                            Text(
                                text = "Stop",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Light,
                                color = Color.White
                            )
                        }
                    }
                }


            }
        }
    }


}


@ExperimentalAnimationApi
fun addAnimation(duration: Int = 600): ContentTransform {
    return slideInVertically(animationSpec = tween(durationMillis = duration)) { height -> height } + fadeIn(
        animationSpec = tween(durationMillis = duration)
    ) with slideOutVertically(animationSpec = tween(durationMillis = duration)) { height -> height } + fadeOut(
        animationSpec = tween(durationMillis = duration)
    )
}

@Preview(backgroundColor = 0xFF333333)
@Composable
fun asdas() {
    StopwatchScreen(stopwatchService = StopwatchService())
}