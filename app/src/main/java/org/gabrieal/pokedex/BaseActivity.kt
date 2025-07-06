package org.gabrieal.pokedex

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBindData()
        setupUI()
        setupViewModel()
        observeResponses()
    }

    fun getSystemBarHeights(view: View, onInsetsAvailable: (statusBarHeight: Int, navigationBarHeight: Int) -> Unit) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val statusBarHeight = systemBars.top
            val navigationBarHeight = systemBars.bottom
            onInsetsAvailable(statusBarHeight, navigationBarHeight)
            insets // return the insets so they're still applied
        }
    }

    open fun setupUI() {}
    open fun setupViewModel() {}
    open fun onBindData() {}
    open fun observeResponses() {}
}