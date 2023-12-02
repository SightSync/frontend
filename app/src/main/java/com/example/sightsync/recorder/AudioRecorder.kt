package com.example.sightsync.recorder

import java.io.File

interface AudioRecorder {
    fun start(outputFile: File)
    fun stop()
}