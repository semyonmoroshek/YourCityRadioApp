package com.smproject.yourcityradioapp

import android.R
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.smproject.yourcityradioapp.databinding.ActivityMainBinding
import java.security.AccessController.getContext


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var stream: String =
        "https://c34.radioboss.fm:18234/stream"

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mediaPlayer = MediaPlayer.create(this, Uri.parse(stream))

        binding.btnPlay.setOnClickListener {
            binding.btnPlay.visibility = View.INVISIBLE
            binding.btnPause.visibility = View.VISIBLE
            mediaPlayer?.start()
        }

        binding.btnPause.setOnClickListener {
            binding.btnPlay.visibility = View.VISIBLE
            binding.btnPause.visibility = View.INVISIBLE
            mediaPlayer?.pause()
        }

        binding.txtHomeLink.setOnClickListener {
            val uri = Uri.parse("http://www.yourcityradio.com")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }


}