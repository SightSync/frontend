package com.example.sightsync

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import coil.compose.rememberImagePainter
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
    private var pictureFile: File? = null
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
                    var imgUri by remember { mutableStateOf(Uri.EMPTY) }

                    val cameraLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.TakePicture()
                    ) {}

                    Button(onClick = {
                        val file = File(cacheDir, "test.jpg")
                        imgUri = FileProvider.getUriForFile(
                            applicationContext,
                            "com.example.sightsync.provider",
                            file
                        )
                        cameraLauncher.launch(imgUri)
                    }, modifier = Modifier) {
                        Text(text = "Test")
                    }
                    Image(
                        modifier = Modifier
                            .padding(16.dp, 8.dp),
                        painter = rememberImagePainter(imgUri),
                        contentDescription = null
                    )
                }
            }
        }
    }
}