package com.example.sightsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.example.sightsync.playback.AndroidAudioPlayer
import com.example.sightsync.recorder.AndroidAudioRecorder
import com.example.sightsync.ui.theme.SightsyncTheme
import java.io.File

class MainActivity : ComponentActivity() {
    private val recorder by lazy {
        AndroidAudioRecorder(applicationContext)
    }
    private val player by lazy {
        AndroidAudioPlayer(applicationContext)
    }
    private var audioFile: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this, arrayOf(
                android.Manifest.permission.RECORD_AUDIO,
            ), 0
        )
        setContent {
            SightsyncTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val recordInteractionSource = remember { MutableInteractionSource() }
                    val isRecordPressed by recordInteractionSource.collectIsPressedAsState()
                    val playInteractionSource = remember { MutableInteractionSource() }
                    val isPlayPressed by playInteractionSource.collectIsPressedAsState()

                    var recordText by remember { mutableStateOf("Record audio") }
                    var playText by remember { mutableStateOf("Play audio") }
                    if (isRecordPressed) {
                        // Pressed
                        recordText = "Recording..."
                        File(cacheDir, "audio.mp3").also {
                            recorder.start(it)
                            audioFile = it
                        }

                        DisposableEffect(Unit) {
                            onDispose {
                                // Released
                                recorder.stop()
                                recordText = "Record audio"
                            }
                        }
                    }
                    if (isPlayPressed) {
                        // Pressed
                        playText = "Playing..."
                        player.playFile(audioFile ?: return@Column)

                        DisposableEffect(Unit) {
                            onDispose {
                                // Released
                                player.stop()
                                playText = "Play audio"
                            }
                        }
                    }
                    Button(onClick = {}, interactionSource = recordInteractionSource) {
                        Text(text = recordText)
                    }
                    Button(onClick = {}, interactionSource = playInteractionSource) {
                        Text(text = playText)
                    }
                }
            }
        }
    }
}