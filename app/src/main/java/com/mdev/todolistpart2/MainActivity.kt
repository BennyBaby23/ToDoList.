package com.mdev.todolistpart2
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/*File name: toDoList Assignment part B
Author Name: Benny Baby
STUDENT ID : 200469127
App Description : CREATE A TODOlIST
Version: Android Studio Dolphin | 2021.3.1 for Windows 64-bit */

class MainActivity : AppCompatActivity() {

    //Variable for each class requirement like db and array
    private lateinit var database: DatabaseReference
    private lateinit var ToDosTasks: MutableList<ToDo>

    //Variable for FAB button
    lateinit var addToDoFAB: FloatingActionButton
    lateinit var todosAdapter: ToDoAdapter

    //Main class function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialization
        database = Firebase.database.reference
        ToDosTasks = mutableListOf<ToDo>()
        todosAdapter = ToDoAdapter(ToDosTasks)

        todosAdapter.onTodoClick = { tvShow, position ->
            showCreateToDoDialog(AlertAction.UPDATE, tvShow, position)
        }

        todosAdapter.onTodoSwipeLeft = { tvShow, position ->
            showCreateToDoDialog(AlertAction.DELETE, tvShow, position)
        }

        initializeRecyclerView()
        initializeFAB()
        addToDoEventListener(database)
//        deleteToDoEventListener(database)
    }


    //function to initialize fab button
    private fun initializeFAB() {
        addToDoFAB = findViewById(R.id.add_List_FAB)
        addToDoFAB.setOnClickListener {
            showCreateToDoDialog(AlertAction.ADD, null, null)
        }
    }

    //function for recycler button
    private fun initializeRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.First_Recycler_View)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = todosAdapter
    }


    //Function for crud
    fun writeNewToDo(toDo: ToDo)
    {
        database.child("TVShows").child(toDo.id.toString()).setValue(toDo)
    }

    //Function for crud
    fun updateToDo(toDo: ToDo)
    {
        database.child("TVShows").child(toDo.id.toString()).setValue(toDo)
    }

    //Function for crud
    fun deleteToDo(toDo: ToDo?)
    {
        database.child("TVShows").child(toDo?.id.toString()).removeValue()
    }

    //Function to create new dialog box to confirm alert
    private fun showCreateToDoDialog(alertAction: AlertAction, toDo: ToDo?, position: Int?) {
        var reminderTitle: String = ""
        var positiveButtonTitle: String = ""
        var negativeButtonTitle: String = getString(R.string.cancel)

        when (alertAction) {
            AlertAction.ADD -> {
                reminderTitle = getString(R.string.dialog_title)
                positiveButtonTitle = getString(R.string.add_todoCRUD)
            }
            AlertAction.UPDATE -> {
                reminderTitle = getString(R.string.update_dialog_title)
                positiveButtonTitle = getString(R.string.update_todoCRUD)
            }
            AlertAction.DELETE -> {
                reminderTitle = ""
                positiveButtonTitle = getString(R.string.delete_todoCRUD)
            }
        }

        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.add_new_todo_list, null)

        builder.setTitle(reminderTitle)
        builder.setView(view)

        val toDoTaskTextView = view.findViewById<TextView>(R.id.ToDo_Task)
        val toDoTaskEditText = view.findViewById<EditText>(R.id.ToDo_EditText)
        val reminderTaskTextView = view.findViewById<TextView>(R.id.Comment)
        val reminderEditText = view.findViewById<EditText>(R.id.Comment_EditText)

        when(alertAction)
        {
            AlertAction.ADD -> {
                builder.setPositiveButton(positiveButtonTitle) { dialog, _ ->
                    dialog.dismiss()
                    val firstCharTitle = toDoTaskEditText.text.toString().substring(0,1)
                    val firstCharStudio = reminderEditText.text.toString().substring(0,1)
                    val id = firstCharTitle + firstCharStudio + System.currentTimeMillis().toString()
                    val newTVShow = ToDo(id, toDoTaskEditText.text.toString(), reminderEditText.text.toString())
                    writeNewToDo(newTVShow)
                }
            }
            AlertAction.UPDATE -> {

                if (toDo != null) {
                    toDoTaskEditText.setText(toDo?.task)
                    reminderEditText.setText(toDo?.reminderDate)
                }

                builder.setPositiveButton(positiveButtonTitle) { dialog, _ ->
                    dialog.dismiss()
                    val newToDo = ToDo(toDo?.id, toDoTaskEditText.text.toString(), reminderEditText.text.toString())
                    updateToDo(newToDo)
                }
            }
            AlertAction.DELETE -> {
                toDoTaskTextView.setText("Delete " + toDo?.task)
                toDoTaskTextView.setTextColor(ContextCompat.getColor(view.context, R.color.red))
                toDoTaskEditText.isVisible = false
                reminderTaskTextView.setText(R.string.comment_prompt)
                reminderEditText.isVisible = false

                builder.setPositiveButton(positiveButtonTitle) { dialog, _ ->
                    dialog.dismiss()
                    deleteToDo(toDo)
                }

                builder.setNegativeButton(negativeButtonTitle) {dialog, _ ->
                    dialog.cancel()
                }
            }
        }
        builder.create().show()
    }

    //Function to add new ToDoTask
    private fun addToDoEventListener(dbReference: DatabaseReference)
    {
        //variable for TodoTask to add to task list
        val ToDoListener = object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                ToDosTasks.clear()
                val toDoDB = dataSnapshot.child("TVShows").children

                //for adding each new task to database
                for(toDoTask in toDoDB)
                {
                    var newToDo = toDoTask.getValue(ToDo::class.java)

                    if(newToDo != null)
                    {
                        ToDosTasks.add(newToDo)
                        todosAdapter.notifyDataSetChanged()
                    }
                }
            }

            //Function for database error
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("Error", "cancelled", databaseError.toException())
            }
        }
        dbReference.addValueEventListener(ToDoListener)
    }


}