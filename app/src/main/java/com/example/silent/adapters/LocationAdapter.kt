package com.example.silent.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.silent.R
import com.example.silent.db.Location

class LocationAdapter(var list: List<Location>, val Clicklistener: ClickListener): RecyclerView.Adapter<LocationViewHolder>(){

    interface ClickListener{
        fun itemclicked(task: Location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_home_recyclerview_layout,parent,false)
        return  LocationViewHolder(view)    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.name?.text = list[position].name
        holder.address?.text = list[position].address
        holder.radius?.text = list[position].radius.toString() + " mile radius"
        if (list[position].status){
            holder.status?.isChecked = true
        }else{
            holder.status?.isChecked = false
        }

        holder.itemView.setOnClickListener {
            println("clicked")
            Clicklistener.itemclicked(list[position])
            true
        }
    }

}