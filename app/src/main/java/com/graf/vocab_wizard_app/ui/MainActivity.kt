package com.graf.vocab_wizard_app.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.graf.vocab_wizard_app.R

class MainActivity : AppCompatActivity() {
    init {
        instance = this
    }
    companion object {
        private var instance: MainActivity? = null;

        fun activityContext() : Context {
            return instance!!.applicationContext
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}