package com.example.silent

import android.content.res.Resources
import android.graphics.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.silent.adapters.LocationAdapter


class SwipetoDelete(private var adapter: LocationAdapter): ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos: Int = viewHolder.adapterPosition
        adapter.deleteItem(pos,viewHolder)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val icon: Bitmap
        val p = Paint()
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            val itemView = viewHolder.itemView
            val height = itemView.bottom.toFloat() - itemView.top.toFloat()
            val width = height / 3

            if (dX > 0) {
                p.color = Color.parseColor("#388E3C")
                val background =
                    RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat())
                c.drawRect(background, p)
                icon = BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.delete_red_foreground)
                val icondest = RectF(
                    itemView.left.toFloat() + width,
                    itemView.top.toFloat() + width,
                    itemView.left.toFloat() + 2 * width,
                    itemView.bottom.toFloat() - width
                )
                c.drawBitmap(icon, null, icondest, p)
            } else {
                p.color = Color.parseColor("#fa315b")
                val background = RectF(
                    itemView.right.toFloat() + dX,
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    (itemView.bottom.toFloat() - 0.05 * width).toFloat()
                )
                c.drawRect(background, p)
                val paint = Paint()
                paint.color = Color.rgb(255,255,255)
                paint.textAlign = Paint.Align.CENTER
                val fontsize = 50
                paint.textSize = fontsize.toFloat()

                c.drawText("Remove", (itemView.right - 150).toFloat(), (itemView.top + itemView.height / 1.8).toFloat(), paint)


            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

}