package com.example.silent.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.example.silent.R
import com.example.silent.db.Location
import com.example.silent.workers.itemWorker
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.user_home_recyclerview_layout.view.*
import kotlinx.android.synthetic.main.user_home_recyclerview_layout.view.name
import java.util.concurrent.TimeUnit

class LocationAdapter(private var list: MutableList<Location>, private val clickListener: (Location,String) -> Unit): RecyclerView.Adapter<LocationAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_home_recyclerview_layout,parent,false)
        return  ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addData(data: MutableList<Location>){
        list = data
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(this.list[position], clickListener)
    }

    fun deleteItem(pos:Int, viewHolder: RecyclerView.ViewHolder){
        //clickListener(list[pos],"swipedelete")

        val data = list[pos]
        list.removeAt(pos)

        notifyItemRemoved(pos)

        val sdata = workDataOf("id" to data.id)

        val repeatingRequest: OneTimeWorkRequest = OneTimeWorkRequest.Builder(itemWorker::class.java).setInputData(sdata).setInitialDelay(2, TimeUnit.SECONDS).build()

        WorkManager.getInstance(viewHolder.itemView.context).beginUniqueWork(data.id, ExistingWorkPolicy.REPLACE, repeatingRequest).enqueue()

        Snackbar.make(viewHolder.itemView.rootView.findViewById(R.id.relative),data.name + " deleting",Snackbar.LENGTH_LONG)
            .setAction("undo") {
                WorkManager.getInstance(viewHolder.itemView.context).cancelUniqueWork(data.id)
                list.add(pos,data)
                notifyItemInserted(pos)
            }.setActionTextColor(Color.YELLOW).show()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        @SuppressLint("SetTextI18n")
        fun bind(data: Location, clickListener: (Location, String) -> Unit) {

            itemView.name.text = data.name
            itemView.address.text = data.address

            // converting meters to mile
            val meterstomile = "%.2f".format(data.radius * 0.000621371192).toDouble()

            itemView.radius.text = "$meterstomile mile"
            itemView.status1.isChecked = data.status
            itemView.setOnClickListener { clickListener(data,"item")}
            itemView.status1.setOnClickListener{ clickListener(data,"switch")}
        }
    }
}