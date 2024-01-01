package com.graf.vocab_wizard_app.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.graf.vocab_wizard_app.R
import java.util.Locale

class DropdownAdapter(
    context: Context,
    resource: Int,
    originalLanguages: List<String>
) : ArrayAdapter<String>(context, resource, originalLanguages.toMutableList()) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    @SuppressLint("DiscouragedApi")
    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_dropdown, parent, false)

        // Set the text to the TextView
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = getItem(position)

        // Set the image
        val imageName = getItem(position)?.lowercase(Locale.ROOT)
        val imageResourceId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
        val imageView = view.findViewById<ImageView>(R.id.imageView)
        imageView.setImageResource(imageResourceId)

        return view
    }
}