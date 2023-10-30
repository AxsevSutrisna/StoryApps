package com.asepssp.storyapps.ui.detail

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.asepssp.storyapps.R
import com.asepssp.storyapps.databinding.ActivityDetailBinding
import com.bumptech.glide.Glide

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBar()
        getIntentDetail()
    }

    private fun setupBar() {
        supportActionBar?.apply {
            title = getString(R.string.bar_detail_title)
            setDisplayHomeAsUpEnabled(true)
            setBackgroundDrawable(ColorDrawable(Color.parseColor("#FEC10E")))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun getIntentDetail() {
        id = intent.getStringExtra(ID) ?: ""
        name = intent.getStringExtra(NAME) ?: ""
        description = intent.getStringExtra(DESCRIPTION) ?: ""
        picture = intent.getStringExtra(PICTURE) ?: ""

        binding.tvDetailName.text = name
        binding.tvDetailDescription.text = description
        Glide.with(this)
            .load(picture)
            .into(binding.ivDetailPhoto)
    }

    companion object {
        const val ID = "ID"
        const val NAME = "NAME"
        const val DESCRIPTION = "DESCRIPTION"
        const val PICTURE = "PICTURE"

        var id: String = ""
        var name: String = ""
        var description: String? = null
        var picture: String? = null
    }
}