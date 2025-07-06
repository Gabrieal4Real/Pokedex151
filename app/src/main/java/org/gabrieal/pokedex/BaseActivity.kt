package org.gabrieal.pokedex

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

    protected fun setupInsets(view: View?) {
        view?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }

    open fun setupUI() {}
    open fun setupViewModel() {}
    open fun onBindData() {}
    open fun observeResponses() {}
}