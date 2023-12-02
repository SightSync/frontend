package com.example.sightsync.playback

import java.io.File

interface AudioPlayer {
    fun playFile(file: File)
    fun stop()
}