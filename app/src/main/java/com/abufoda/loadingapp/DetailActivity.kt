package com.abufoda.loadingapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationManagerCompat
import com.abufoda.loadingapp.databinding.ActivityDetailBinding
import com.abufoda.loadingapp.databinding.ContentDetailBinding
import timber.log.Timber
import kotlin.properties.Delegates

class DetailActivity : AppCompatActivity() {

    private lateinit var fileName: String
    private var status by Delegates.notNull<Boolean>()

    private lateinit var binding: ActivityDetailBinding
    private lateinit var contentBinding: ContentDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        contentBinding = binding.content

        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.toolbar)

        fileName = intent.getStringExtra(FILENAME_EXTRA).toString()
        status = intent.getBooleanExtra(STATUS_EXTRA, false)
        Timber.i("%S: %b", fileName, status)

        contentBinding.fileName.text = fileName
        contentBinding.status.text = status.toString()
        contentBinding.status.setTextColor(if (status) getColor(R.color.green) else Color.RED)

        contentBinding.button.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val notificationManagerCompat = NotificationManagerCompat.from(this)
        notificationManagerCompat.cancel(0)
    }
    companion object {
        const val FILENAME_EXTRA = "filename"
        const val STATUS_EXTRA = "status"
    }
}