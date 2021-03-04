package com.ricardocanales.notesrcm

interface NoteSelectionListener {
    fun onNoteSelected(noteIndex : Int)
    fun onNoteSelectedForDelete(noteIndex : Int)
}