package com.example.week64

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.week64.ROOM.Task
import kotlinx.android.synthetic.main.item_task.view.*

class TaskAdapter (var items: ArrayList<Task>, val context: Context) : RecyclerView.Adapter<TaskViewHolder>() {

    lateinit var mListener: TaskItemClickListener

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TaskViewHolder {
        return TaskViewHolder(LayoutInflater.from(context).inflate(R.layout.item_task, p0, false))
    }

    override fun getItemCount(): Int {
        return  items.size
    }

    override fun onBindViewHolder(p0: TaskViewHolder, p1: Int) {
        p0.tvId.text = "#${p1.toString()}"
        p0.tvTask.text = items[p1].description

        if(items[p1].user_uid != null && items[p1].username != "")
        {
            p0.tvAsign.text = "Assigned to: ${items[p1].username}"

            Log.i("aaaaa", p0.tvAsign.text.toString())
            //p0.tvAsign.text = "Assigned to: ${items[p1].user_uid.toString()}"
        }
        else
        {
            p0.tvAsign.text = "Assigned to: Unassigned"
        }

        p0.itemView.setOnClickListener {
            mListener.onItemClicked(p1)
        }

        p0.itemView.setOnLongClickListener {
            mListener.onItemLongClicked(p1)
            true
        }
    }

    fun setListener(listener: TaskItemClickListener)
    {
        this.mListener = listener
    }

    fun appenData(new: Task)
    {
        this.items.add(new)
        notifyItemInserted(items.size - 1)
    }

    fun removeData(position: Int)
    {
        this.items.removeAt(position)
        notifyItemMoved(position, position)
    }

    fun setData(items: ArrayList<Task>){
        this.items = items
        notifyDataSetChanged()
    }
}

class TaskViewHolder(view : View) :RecyclerView.ViewHolder(view) {
    var tvTask = view.tvTask
    var tvId = view.tvId
    var tvAsign = view.tvAsign

}