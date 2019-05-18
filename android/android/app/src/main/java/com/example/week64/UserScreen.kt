package com.example.week64

import android.arch.persistence.room.Room
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.user_screen.*
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.example.week64.ROOM.AppDatabase
import com.example.week64.ROOM.User
import com.example.week64.ROOM.UserDAO
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.schedule


class UserScreen : AppCompatActivity() {
    var Users: ArrayList<User> = ArrayList()
    lateinit var userAdapter: UserAdapter
    lateinit var dao: UserDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_screen)
        initRoomDatabase()
        setupRecyclerView()
        getUser()
        btnAdd.setOnClickListener {
            var user = User()

            val strTemp = edtUserID.text.toString()

            // not null and does not available before
            if(strTemp != "" && detectUserAvailable(strTemp))
            {
                user.name = strTemp
                val id = dao.insert(user)
                user.id = id.toInt()
                userAdapter.appenData(user)
                edtUserID.setText("")
            }
            // db1 = AppDatabase.invoke(this)

        }
    }

    private fun initRoomDatabase() {
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            DATABASE_NAME
        ).allowMainThreadQueries()
            .build()
        dao = db.userDAO()
    }

    private fun setupRecyclerView() {
        rvUser.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        userAdapter = UserAdapter(Users, this)
        rvUser.adapter = userAdapter
        userAdapter.setListener(btRemove)
        userAdapter.notifyDataSetChanged() // notify data changed

    }

    private val btRemove = object : ItemClickListener {
        override fun btRemoveClicked(position: Int) {
            Log.e("Users[position]", Users[position].toString())
            dao.delete(Users[position])

            userAdapter.removeItem(Users[position], position)

            Timer(false).schedule(400) {
                runOnUiThread {
                    userAdapter.notifyDataSetChanged()
                }
            }

        }

    }

    private fun getUser() {
        val users = dao.getAll() // get Tasks from ROOM database
        Log.i("User: ", users.toString())
        this.Users.addAll(users) // add to task list
        userAdapter.notifyDataSetChanged() // notify data changed
    }

    private fun detectUserAvailable(name : String) : Boolean
    {
        val size = Users.size
        if (size > 0)
        {
            for (i in 0 until size)
            {
                if(Users[i].name == name)
                {
                    Toast.makeText(this, "User was available.", Toast.LENGTH_SHORT).show()
                    return false
                }
            }
        }

        return true
    }


}