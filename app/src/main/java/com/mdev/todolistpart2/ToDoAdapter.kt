package com.mdev.todolistpart2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ToDoAdapter(private val dataSet: MutableList<ToDo>):
    RecyclerView.Adapter<ToDoAdapter.ViewHolder>() {

    var onTodoClick: ((ToDo, position: Int)-> Unit)? = null
    var onTodoSwipeLeft: ((ToDo, position: Int)-> Unit)? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val title: TextView
        val studio: TextView

        init {

            title = view.findViewById(R.id.ToDo_List)
            studio = view.findViewById(R.id.comment)

            view.setOnTouchListener(object: CustomTouchListener(view.context){
                override fun onSwipeLeft() {
                    super.onSwipeLeft()
                    onTodoSwipeLeft?.invoke(dataSet[adapterPosition], adapterPosition)
                }

                override fun onClick() {
                    super.onClick()
                    onTodoClick?.invoke(dataSet[adapterPosition], adapterPosition)
                }
            })
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.text_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.title.text = dataSet[position].title // index of the array
        viewHolder.studio.text = dataSet[position].studio
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}