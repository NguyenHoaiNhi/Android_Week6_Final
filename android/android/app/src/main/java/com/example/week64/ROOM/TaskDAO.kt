package com.example.week64.ROOM

import android.arch.persistence.room.*

@Dao
interface TaskDAO {
    @Query("SELECT * FROM task")
    fun getAll():List<Task>

    @Query("SELECT * FROM task WHERE id=:id")
    fun findById(id: Int): Task

    @Query("SELECT * FROM task WHERE description=:description")
    fun findByDecription(description: String): Task

    @Query("SELECT * FROM task WHERE task.completed LIKE '1'")
    fun finByCompleted(): List<Task>

    @Insert
    fun insertAll(vararg todo: Task) : List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(obj: Task):Long

    @Delete
    fun delete(toto: Task)

    @Update
    fun update(task : Task)

    @Query("DELETE FROM task")
    fun deleteAllTask()
}


@Dao
interface UserDAO{
    @Query("SELECT * FROM User")
    fun getAll(): List<User>

    @Query("SELECT *FROM User WHERE name LIKE :name")
    fun findByName(name: String) : User

    @Query("SELECT * FROM task WHERE id =:id")
    fun findById(id: Int): User

    @Insert
    fun insertAll(vararg todo: User) : List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(obj: User): Long

    @Delete
    fun delete(todo: User)

    @Update
    fun update( Task: User)

    @Query("DELETE FROM User")
    fun deleteAllUser()
}