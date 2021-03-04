package com.ricardocanales.notesrcm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ricardocanales.notesrcm.model.Notes
import java.util.ArrayList

class CustomAdapter(private val notes: ArrayList<Notes>,
                    private val noteSelectionListener: NoteSelectionListener) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_note_cell, parent, false)
        return ViewHolder(view,noteSelectionListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentNote = notes[position]
        holder.bindCell(currentNote)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    fun updateNotes(updatedNote: ArrayList<Notes>){
        notes.clear()
        notes.addAll(updatedNote)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View,
                     private val noteSelectionListener: NoteSelectionListener) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener,View.OnLongClickListener{

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            noteSelectionListener.onNoteSelected(this.adapterPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            noteSelectionListener.onNoteSelectedForDelete(adapterPosition)
            return true
        }

        fun bindCell(note : Notes){
            val noteTitleTextView = itemView.findViewById(R.id.note_title_text_view) as TextView
            val noteImportanceTextView = itemView.findViewById(R.id.importance_state) as ImageView
            val noteResumeTextView = itemView.findViewById(R.id.resume_note_text_view) as TextView
            noteTitleTextView.text = note.noteTitle
            noteResumeTextView.text = note.noteResume
            when(note.noteImportance){
                true -> noteImportanceTextView.setImageDrawable(ContextCompat.getDrawable(itemView.context,R.drawable.rate_star_big_on_holo_dark))
                else -> noteImportanceTextView.setImageDrawable(ContextCompat.getDrawable(itemView.context,R.drawable.rate_star_big_off_holo_dark))
            }
        }
    }
}