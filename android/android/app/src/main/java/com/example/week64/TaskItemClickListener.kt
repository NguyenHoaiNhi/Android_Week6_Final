package com.example.week64

import java.text.FieldPosition

interface TaskItemClickListener {
    fun onItemClicked(position: Int)
    fun onItemLongClicked(position: Int)
}