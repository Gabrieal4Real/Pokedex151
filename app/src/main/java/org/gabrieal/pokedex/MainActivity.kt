package org.gabrieal.pokedex

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {

    private val llRoot by lazy { findViewById<LinearLayout>(R.id.ll_root) }
    private val ivPokedex by lazy { findViewById<ImageView>(R.id.iv_pokedex) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupInsets()
        setupViewModel()
        setupUI()
    }

    private fun setupUI() {

    }

    private fun setupViewModel() {


    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(llRoot) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}