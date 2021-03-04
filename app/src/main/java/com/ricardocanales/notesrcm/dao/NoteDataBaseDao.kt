package com.ricardocanales.notesrcm.dao

import androidx.room.*
import com.ricardocanales.notesrcm.model.Notes

@Dao
interface NoteDataBaseDao {
    @Insert
    fun insertNote(newNote : Notes)

    @Update
    fun updateNote(note : Notes)

    @Delete
    fun deleteNote(note : Notes)

    @Query("SELECT * FROM table_notes order by note_importance desc")
    fun getAllNotes(): List<Notes>

    @Query("SELECT * FROM table_notes WHERE noteId= :key")
    fun getNote(key: Long): Notes?
}