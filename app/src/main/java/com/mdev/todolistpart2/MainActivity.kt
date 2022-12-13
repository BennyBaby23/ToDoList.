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

    private lateinit var database: DatabaseReference
    private lateinit var ToDosTasks: MutableList<ToDo>

    lateinit var addToDoFAB: FloatingActionButton
    lateinit var todosAdapter: ToDoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialization
        database = Firebase.database.reference
        ToDosTasks = mutableListOf<ToDo>()
        todosAdapter = ToDoAdapter(ToDosTasks)

        todosAdapter.onTodoClick = { tvShow, position ->
            showCreateTVShowDialog(AlertAction.UPDATE, tvShow, position)
        }

        todosAdapter.onTodoSwipeLeft = { tvShow, position ->
            showCreateTVShowDialog(AlertAction.DELETE, tvShow, position)
        }

        initializeRecyclerView()
        initializeFAB()
        addTVShowEventListener(database)
    }


    private fun initializeFAB() {
        addToDoFAB = findViewById(R.id.add_List_FAB)
        addToDoFAB.setOnClickListener {
            showCreateTVShowDialog(AlertAction.ADD, null, null)
        }
    }

    private fun initializeRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.First_Recycler_View)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = todosAdapter
    }

    fun writeNewTVShow(tvShow: ToDo)
    {
        database.child("TVShows").child(tvShow.id.toString()).setValue(tvShow)
    }

    fun updateTVShows(tvShow: ToDo)
    {
        database.child("TVShows").child(tvShow.id.toString()).setValue(tvShow)
    }

    fun deleteTVShow(tvShow: ToDo?)
    {
        database.child("TVShows").child(tvShow?.id.toString()).removeValue()
    }

    private fun showCreateTVShowDialog(alertAction: AlertAction, tvShow: ToDo?, position: Int?) {
        var dialogTitle: String = ""
        var positiveButtonTitle: String = ""
        var negativeButtonTitle: String = getString(R.string.cancel)

        when (alertAction) {
            AlertAction.ADD -> {
                dialogTitle = getString(R.string.dialog_title)
                positiveButtonTitle = getString(R.string.add_todoCRUD)
            }
            AlertAction.UPDATE -> {
                dialogTitle = getString(R.string.update_dialog_title)
                positiveButtonTitle = getString(R.string.update_todoCRUD)
            }
            AlertAction.DELETE -> {
                dialogTitle = ""
                positiveButtonTitle = getString(R.string.delete_todoCRUD)
            }
        }

        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.add_new_todo_list, null)

        builder.setTitle(dialogTitle)
        builder.setView(view)

        val tvShowTitleTextView = view.findViewById<TextView>(R.id.ToDo_Task)
        val tvShowTitleEditText = view.findViewById<EditText>(R.id.ToDo_EditText)
        val studioNameTextView = view.findViewById<TextView>(R.id.Comment)
        val studioNameEditText = view.findViewById<EditText>(R.id.Comment_EditText)

        when(alertAction)
        {
            AlertAction.ADD -> {
                builder.setPositiveButton(positiveButtonTitle) { dialog, _ ->
                    dialog.dismiss()
                    val firstCharTitle = tvShowTitleEditText.text.toString().substring(0,1)
                    val firstCharStudio = studioNameEditText.text.toString().substring(0,1)
                    val id = firstCharTitle + firstCharStudio + System.currentTimeMillis().toString()
                    val newTVShow = ToDo(id, tvShowTitleEditText.text.toString(), studioNameEditText.text.toString())
                    writeNewTVShow(newTVShow)
                }
            }
            AlertAction.UPDATE -> {

                if (tvShow != null) {
                    tvShowTitleEditText.setText(tvShow?.title)
                    studioNameEditText.setText(tvShow?.studio)
                }

                builder.setPositiveButton(positiveButtonTitle) { dialog, _ ->
                    dialog.dismiss()
                    val newTVShow = ToDo(tvShow?.id, tvShowTitleEditText.text.toString(), studioNameEditText.text.toString())
                    updateTVShows(newTVShow)
                }
            }
            AlertAction.DELETE -> {
                tvShowTitleTextView.setText("Delete " + tvShow?.title)
                tvShowTitleTextView.setTextColor(ContextCompat.getColor(view.context, R.color.red))
                tvShowTitleEditText.isVisible = false
                studioNameTextView.setText(R.string.comment_prompt)
                studioNameEditText.isVisible = false

                builder.setPositiveButton(positiveButtonTitle) { dialog, _ ->
                    dialog.dismiss()
                    deleteTVShow(tvShow)
                }

                builder.setNegativeButton(negativeButtonTitle) {dialog, _ ->
                    dialog.cancel()
                }
            }
        }
        builder.create().show()
    }

    private fun addTVShowEventListener(dbReference: DatabaseReference)
    {
        val TVShowListener = object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                ToDosTasks.clear()
                val tvShowDB = dataSnapshot.child("TVShows").children

                for(tvShow in tvShowDB)
                {
                    var newShow = tvShow.getValue(ToDo::class.java)

                    if(newShow != null)
                    {
                        ToDosTasks.add(newShow)
                        todosAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("tvShowError", "loadTVShow:cancelled", databaseError.toException())
            }
        }
        dbReference.addValueEventListener(TVShowListener)
    }

}