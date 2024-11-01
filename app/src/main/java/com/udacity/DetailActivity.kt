package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val repositoryName = intent.getStringExtra("FILE_NAME") ?: "Unknow file name"
        val downloadStatus = intent.getStringExtra("DOWNLOAD_STATUS") ?: "Unknow status"

        binding.contentDetail.fileName.text = repositoryName
        binding.contentDetail.downloadStatus.text = downloadStatus

        binding.contentDetail.returnButton.setOnClickListener {
            finish()
        }


    }
}
