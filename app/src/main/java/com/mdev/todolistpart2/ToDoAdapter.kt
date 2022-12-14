package com.mdev.todolistpart2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
/*File name: toDoList Assignment part B
Author Name: Benny Baby
STUDENT ID : 200469127
App Description : CREATE A TODOlIST
Version: Android Studio Dolphin | 2021.3.1 for Windows 64-bit */
class ToDoAdapter(private val dataSet: MutableList<ToDo>):
    RecyclerView.Adapter<ToDoAdapter.ViewHolder>() {

    //Variable for gesture function
    var onTodoClick: ((ToDo, position: Int)-> Unit)? = null
    var onTodoSwipeLeft: ((ToDo, position: Int)-> Unit)? = null

    //function for each gesture on touchListener
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val task: TextView
        val reminderDate: TextView

        init {

            task = view.findViewById(R.id.ToDo_List)
            reminderDate = view.findViewById(R.id.comment)

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

    //function for onCreate viewHolder
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.text_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.task.text = dataSet[position].task // index of the array
        viewHolder.reminderDate.text = dataSet[position].reminderDate
    }

    //Function return datasetSize
    override fun getItemCount(): Int {
        return dataSet.size
    }
}