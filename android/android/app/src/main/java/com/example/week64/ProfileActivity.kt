package com.example.week64

import android.arch.persistence.room.Room
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.example.week64.ROOM.*
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.ArrayList


class ProfileActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var tasks: ArrayList<Task> = ArrayList()
    lateinit var dao: TaskDAO
    var task = Task()
    var username: ArrayList<String?> = ArrayList()
    lateinit var daoUser: UserDAO
    var user = User()

    var description : String = "a"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initRoomDatabase()
        getTasks()

        getAndPutData()

        setupSpiner()
    }

    private fun initRoomDatabase(){
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, DATABASE_NAME
        ).allowMainThreadQueries().build()

        dao = db.taskDAO()
        daoUser = db.userDAO()
    }

    private fun getTasks()
    {
        val tasks = dao.getAll()
        this.tasks.addAll(tasks)
    }

    private fun getAndPutData() {
        val data = intent.extras

        if (data != null) {
            description = data.getString(DESCRIPTION_KEY)
            val user_uid = data.getInt(USER_KEY)
            val completed : Boolean = data.getBoolean(COMPLETED_KEY)

            // Data available here
            tvTitle.text = description
            cbCompleted.isChecked = completed

            if(user_uid != 0)
            {
                tvAssign.text = user_uid.toString()
            }

            // Check change state of check box
            cbCompleted.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->

                // set database
                task = dao.findByDecription(description)

                task.completed = cbCompleted.isChecked

//                var mStr :String = "unassigned"
//
//                if(task.completed)  mStr = "assigned"
//
//                Toast.makeText(this, "Task is $mStr!", Toast.LENGTH_SHORT).show()

                // Update to Database
                dao.update(task)
            })
        }
    }

    private fun setupSpiner() {
        val users: List<User> = daoUser.getAll()
        //Toast.makeText(this, users[0].name, Toast.LENGTH_SHORT).show()
        for (i in users){
            username.add(i.name)
            Log.d("name", i?.name)
        }

        var spinner:Spinner? = null

        spinner = this.spUser
        spinner!!.setOnItemSelectedListener(this)

        // Create an ArrayAdapter using a simple spinner layout and languages array
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, username)
        // Set layout to use when the list of choices appear
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        spinner!!.setAdapter(aa)
    }
    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {

        task = dao.findByDecription(description)

        task.user_uid = position
        task.username = username[position].toString()

        tvAssign.text ="User "+ username[position]

        // Update to Database
        dao.update(task)
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {
        Toast.makeText(this, "Task Detail", Toast.LENGTH_SHORT).show()
    }

}