package com.graf.vocab_wizard_app.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.graf.vocab_wizard_app.R

class MainActivity : AppCompatActivity() {
    init {
        instance = this
    }
    companion object {
        private var instance: MainActivity? = null

        fun activityContext() : Context {
            return instance!!.applicationContext
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if the device is connected to the internet
        if (!isConnectedToInternet()) {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    private fun isConnectedToInternet(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        if (connectivityManager != null) {
                val network = connectivityManager.activeNetwork
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
        }

        return false
    }
}