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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.example.sightsync.api.other.SttService
import com.example.sightsync.recorder.AndroidAudioRecorder
import com.example.sightsync.ui.theme.SightsyncTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class MainActivity : ComponentActivity() {
    private val recorder by lazy {
        AndroidAudioRecorder(applicationContext)
    }

    private var audioFile: File? = null

    private val sttAPI by lazy {
        SttService().sttAPI
    }

    private fun uploadAudio() {
        audioFile?.let {
            GlobalScope.launch(Dispatchers.IO) {
                val requestFile = RequestBody.create(MediaType.parse("audio/*"), audioFile!!)
                val audioPart =
                    MultipartBody.Part.createFormData("audio", audioFile!!.name, requestFile)
                val call = sttAPI.getTranscription(audioPart).execute()
                if (call.isSuccessful) {
                    val transcription = call.body()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this, arrayOf(
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.CAMERA,
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
                    if (isRecordPressed) {
                        File(cacheDir, "audio.mp3").also {
                            recorder.start(it)
                            audioFile = it
                        }
                        DisposableEffect(Unit) {
                            onDispose {
                                recorder.stop()
                                uploadAudio()
                            }
                        }
                    }
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxSize(),
                        interactionSource = recordInteractionSource
                    ) {
                    }
                }
            }
        }
    }
}