package com.example.sightsync

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
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
import androidx.core.content.ContextCompat
import com.example.sightsync.api.cog.PostCaptionServiceCog
import com.example.sightsync.api.cog.PostImageServiceCog
import com.example.sightsync.api.other.PostImageService
import com.example.sightsync.api.other.SttService
import com.example.sightsync.api.other.TtsService
import com.example.sightsync.playback.AndroidAudioPlayer
import com.example.sightsync.recorder.AndroidAudioRecorder
import com.example.sightsync.ui.theme.SightsyncTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

fun reduceImageFileQuality(imageFile: File, quality: Int): File {
    val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
    val stream = FileOutputStream(imageFile)
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
    stream.flush()
    stream.close()
    return imageFile
}

class MainActivity : ComponentActivity() {
    private val recorder by lazy {
        AndroidAudioRecorder(applicationContext)
    }
    private val player by lazy {
        AndroidAudioPlayer(applicationContext)
    }

    private var audioFile: File? = null

    private var imageFile: File? = null

    private var apiImageId: String? = null

    private var apiImageCogId: String? = null
    private var cogCaption: String? = null

    private val sttAPI by lazy {
        SttService().sttAPI
    }

    private val ttsAPI by lazy {
        TtsService().ttsAPI
    }

    private val postImageAPI by lazy {
        PostImageService().postImageAPI
    }

    private val postImageCogAPI by lazy {
        PostImageServiceCog().postImageAPI
    }

    private val postCaptionCogAPI by lazy {
        PostCaptionServiceCog().postCaptionAPI
    }

    private var transcription: String? = "Hello World"

    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            imageCapture = ImageCapture.Builder()
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, imageCapture
                )
            } catch (exc: Exception) {
                // Handle exception
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePicture() {
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
            createImageFile() // Method to create an image file in external storage
        ).build()

        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    imageFile =
                        File(output.savedUri?.path) // Assign the taken image to the imageFile variable
                    imageFile = reduceImageFileQuality(imageFile!!, 30)
                    uploadImage()
                }

                override fun onError(exc: ImageCaptureException) {
                    // Handle exception
                }
            }
        )
    }

    private lateinit var currentPhotoPath: String

    private fun createImageFile(): File {
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "picture",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun uploadAudio() {
        audioFile?.let {
            GlobalScope.launch(Dispatchers.IO) {
                val requestFile = RequestBody.create(MediaType.parse("audio/*"), audioFile!!)
                val audioPart =
                    MultipartBody.Part.createFormData("audio", audioFile!!.name, requestFile)
                val call = sttAPI.getTranscription(audioPart).execute()
                if (call.isSuccessful) {
                    transcription = call.body()
                }
            }
        }
    }

    private fun uploadImage() {
        imageFile?.let {
            GlobalScope.launch(Dispatchers.IO) {
                val requestFile = RequestBody.create(MediaType.parse("image/jpg"), imageFile!!)
                val imagePart =
                    MultipartBody.Part.createFormData("image", imageFile!!.name, requestFile)
                val call = postImageAPI.postImage(imagePart).execute()
                val callCog = postImageCogAPI.postImage(imagePart).execute()
                if (call.isSuccessful) {
                    apiImageId = call.body()
                }
                if (callCog.isSuccessful) {
                    apiImageCogId = callCog.body()
                    var prompt = "Describe the image"
                    if (transcription != null && transcription != "") {
                        prompt = transcription!!
                    }
                    val captionCall = postCaptionCogAPI.postCaption(apiImageCogId!!, prompt).execute()
                    if (captionCall.isSuccessful) {
                        cogCaption = captionCall.body()
                        val ttsCall = ttsAPI.getAudio(cogCaption!!).execute()
                        if (ttsCall.isSuccessful) {
                            var audioFile: File?
                            ttsCall.body()?.byteStream()?.use { input ->
                                audioFile = File(cacheDir, "tts.mp3")
                                audioFile!!.outputStream().use { output ->
                                    input.copyTo(output)
                                }.also {
                                    player.playFile(audioFile!!)
                                }
                            }
                        }
                    }
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
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
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
                            player.stop()
                        }
                        DisposableEffect(Unit) {
                            onDispose {
                                recorder.stop()
                                uploadAudio()
                                takePicture()
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
        startCamera()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}