package com.ricardocanales.notesrcm.model

import android.graphics.drawable.Drawable
import android.media.Image
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "table_notes")
data class Notes(

    @ColumnInfo(name= "note_title")
    val noteTitle: String,

    @ColumnInfo(name= "note_importance")
    val noteImportance: Boolean,

    @ColumnInfo(name= "note_resume")
    val noteResume: String,

/*    @ColumnInfo(name= "note_image")
    val noteImage: Int,*/

    @PrimaryKey(autoGenerate = true)
    val noteId: Long = 0L): Serializable