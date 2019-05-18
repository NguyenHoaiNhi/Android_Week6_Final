package com.example.week64

import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import com.example.week64.ROOM.AppDatabase
import com.example.week64.ROOM.Task
import com.example.week64.ROOM.TaskDAO
import com.facebook.stetho.Stetho
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {

    var tasks: ArrayList<Task> = ArrayList()
    lateinit var taskAdapter: TaskAdapter

    lateinit var dao: TaskDAO

    var task = Task()

    lateinit var db: AppDatabase

    private lateinit var mHandler: Handler
    private lateinit var mRunnable:Runnable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Stetho.initializeWithDefaults(this)
        initRoomDatabase()

        setupRecycleview()

        getTasks()



        db = AppDatabase.invoke(this) // get Room database instance

        // get reference to ImageView
        val ivAdd = findViewById(R.id.ivAdd) as ImageView
        // set on-click listener
        ivAdd.setOnClickListener {
            // your code to perform when the user clicks on the ImageView

            val strTemp = etTask.text.toString()

            // not null and does not available before
            if(strTemp != "" && detectTaskAvailable(strTemp))
            {
                task.description = strTemp
                taskAdapter.appenData(task)
                dao.insert(task)

                //Toast.makeText(this@MainActivity, "You added a task.", Toast.LENGTH_SHORT).show()
                etTask.setText("")

//                task.description = strTemp
//
//                val taskDAO = db.taskDAO()
//                val id = taskDAO.insert(task)
//
//                task.id = id.toInt()
//
//                taskAdapter.appenData(task)
//                dao.insert(task)
//
//                //Toast.makeText(this@MainActivity, "You added a task.", Toast.LENGTH_SHORT).show()
//                etTask.setText("")
            }

        }


        ivAdd.setOnLongClickListener {
            // Remove all task here

            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("Confirmation")
                .setMessage("Remove all task?")
                .setPositiveButton("OK"){_,_->
                    dao.deleteAllTask()

                    val size = tasks.size
                    if (size > 0)
                    {
                        for (i in 0 until size)
                        {
                            tasks.removeAt(0)
                        }

                        taskAdapter.notifyItemRangeRemoved(0, size)
                    }
                }
                .setNegativeButton("Cancel")
                {
                        dialog, _ -> dialog?.dismiss()
                }

            val myDialog = builder.create()
            myDialog.show()

            return@setOnLongClickListener false
        }
        btPlus.setOnClickListener(toUserScreen)
        radioGroupProccess()

        reFreshMainScreen()
    }

    private fun reFreshMainScreen() {
        swipeContainer.setOnRefreshListener {

            // Clear screen
            tasks.removeAll(tasks)
            taskAdapter.notifyDataSetChanged()

            // Reload data
            var mTtasks = dao.getAll()
            tasks.addAll(mTtasks)

            // Update data
            taskAdapter.notifyDataSetChanged()


            // Initialize the handler instance
            mHandler = Handler()

            mRunnable = Runnable {
                // Hide swipe to refresh icon animation
                swipeContainer.isRefreshing = false
            }

            // Execute the task after specified time
            mHandler.postDelayed(
                mRunnable, 100

            )
        }
    }


    private fun radioGroupProccess() {
        val radioGroup = findViewById<RadioGroup>(R.id.rdGroup)
        radioGroup?.setOnCheckedChangeListener { group, checkedId ->

            // Clear screen
            tasks.removeAll(tasks)
            taskAdapter.notifyDataSetChanged()

            // Reload data
            var mTtasks = dao.getAll()
            tasks.addAll(mTtasks)

            // Choose true data to show on screen
            val size = tasks.size
            var j = 0

            if (R.id.rdCompleted == checkedId)
            {
                if (size > 0) {
                    for (i in 0 until size)
                    {
                        if (tasks[j].completed == false)
                        {
                            tasks.removeAt(j)
                            taskAdapter.notifyItemRemoved(j)
                        }
                        else
                        {
                            j++
                        }
                    }
                }
            }
            else if (R.id.rdUncompleted == checkedId)
            {
                if (size > 0) {
                    for (i in 0 until size)
                    {
                        if (tasks[j].completed == true)
                        {
                            tasks.removeAt(j)
                            taskAdapter.notifyItemRemoved(j)
                        }
                        else
                        {
                            j++
                        }
                    }
                }
            }
            else (R.id.rdAll == checkedId)
            {
            }

            // Update data
            taskAdapter.notifyDataSetChanged()
        }
    }

    private fun initRoomDatabase(){
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, DATABASE_NAME
        ).allowMainThreadQueries().build()

        dao = db.taskDAO()
    }


    private fun setupRecycleview()
    {
        rvTask.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?

        taskAdapter = TaskAdapter(tasks, this)

        rvTask.adapter = taskAdapter

        taskAdapter.setListener(taskItemClickListener)
    }
    private val toUserScreen = View.OnClickListener {
        val intent = Intent(this, UserScreen::class.java)
        startActivityForResult(intent, REQUEST_SETTING_USER_SCREEN)
    }
    companion object {
        const val REQUEST_SETTING_USER_SCREEN = 1099
    }
    private fun getTasks()
    {
        val tasks = dao.getAll()
        this.tasks.addAll(tasks)
        taskAdapter.notifyDataSetChanged()
    }


    private val taskItemClickListener = object : TaskItemClickListener{
        override fun onItemClicked(position: Int) {
//            Toast.makeText(this@MainActivity, "Task ${tasks[position].description} clicked.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@MainActivity, ProfileActivity::class.java)
            intent.putExtra(DESCRIPTION_KEY, tasks[position].description)
            intent.putExtra(COMPLETED_KEY, tasks[position].completed)
            intent.putExtra(USER_KEY, tasks[position].user_uid)
            startActivity(intent)
        }

        override fun onItemLongClicked(position: Int) {
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("Confirmation")
                .setMessage("Remove ${tasks[position].description} ?")
                .setPositiveButton("OK"){_,_->
                    removeItem(position)
                }
                .setNegativeButton(
                    "Cancel"
                ){dialog, _ -> dialog?.dismiss() }

            val myDialog = builder.create()
            myDialog.show()
        }
    }

    private fun removeItem(position: Int) {

        dao.delete(tasks[position]) // remove from Room database  //
        tasks.removeAt(position)

        taskAdapter.notifyItemRemoved(position)
        Timer(false).schedule(500)
        {
            runOnUiThread{
                taskAdapter.setData(tasks)
                taskAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun detectTaskAvailable(description : String) : Boolean
    {
        val size = tasks.size
        if (size > 0)
        {
            for (i in 0 until size)
            {
                if(tasks[i].description == description)
                {
                    Toast.makeText(this@MainActivity, "Task was available.", Toast.LENGTH_SHORT).show()
                    return false
                }
            }
        }

        return true
    }

}
