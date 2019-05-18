package com.example.week64.ROOM

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = "task")
data class Task(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var description: String,
    var completed: Boolean,
    var user_uid: Int?,
    var username: String
): Parcelable {
    constructor(): this(null, "", false, null, "")
}
