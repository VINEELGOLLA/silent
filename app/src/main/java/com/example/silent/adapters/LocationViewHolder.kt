package com.example.silent.adapters

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.silent.R
import com.google.android.material.switchmaterial.SwitchMaterial

class LocationViewHolder(itemview: View): RecyclerView.ViewHolder(itemview) {
    val name = itemview.findViewById<TextView>(R.id.name)
    val address = itemview.findViewById<TextView>(R.id.address)
    val radius = itemview.findViewById<TextView>(R.id.radius)
    val status = itemview.findViewById<SwitchMaterial>(R.id.status1)
}