package com.mdev.todolistpart2
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

/*File name: toDoList Assignment part B
Author Name: Benny Baby
STUDENT ID : 200469127
App Description : CREATE A TODOlIST
Version: Android Studio Dolphin | 2021.3.1 for Windows 64-bit */

//Value for dataset to store task and due date or comments
@IgnoreExtraProperties
data class ToDo(
    val id: String? = "",
    val task: String? = "",
    val reminderDate: String? = ""
){
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "task" to task,
            "dueDate" to reminderDate
        )
    }
}

