package com.ricardocanales.notesrcm

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ricardocanales.notesrcm.database.LocalDataBase
import com.ricardocanales.notesrcm.model.Notes
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), NoteSelectionListener {

    companion object{
        var notesList : ArrayList<Notes> = arrayListOf()
        var indicator : Int = 0
    }

    private var customAdapter: CustomAdapter? = null
    private var detailActivity: DetailActivity? = null

    var recentlyDeletedNote : Notes? = null
    var recentlyDeletedNoteIndex : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        populateList()

        my_recycler_view.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL,false)

        customAdapter = CustomAdapter(notesList,this)

        getAllNotes()

        //assign adapter
        my_recycler_view.adapter = customAdapter

    }
    private fun populateList(){
        add_note_button.setOnClickListener(){
            indicator = 0
            showDialog(getString(R.string.msg_add), DialogInterface.OnClickListener{ _, _ ->
                startActivity(Intent(this,DetailActivity::class.java))
            })
        }
    }

    override fun onNoteSelected(noteIndex: Int) {
        indicator = 1
        val noteSelected = notesList[noteIndex]
        val detailIntent = Intent(this,DetailActivity::class.java)

        detailIntent.putExtra("noteSelected",noteSelected)
        startActivity(detailIntent)
    }

    override fun onNoteSelectedForDelete(noteIndex: Int) {
        recentlyDeletedNote = notesList[noteIndex]
        recentlyDeletedNoteIndex = noteIndex
        this.showDialog(getString(R.string.msg_delete) +" "+ recentlyDeletedNote!!.noteTitle.toString()+"?", DialogInterface.OnClickListener{ _, _ ->
            deleteNote(notesList[noteIndex])
        })
    }

    fun showDialog(message : String, listener: DialogInterface.OnClickListener){
        val dialogFragment = CustomDialog(message, listener)
        dialogFragment.show(supportFragmentManager,"dialog")
    }

    fun showSnackBar(message: String){
        val mySnackBar = Snackbar.make(findViewById(R.id.my_coordinator),message,Snackbar.LENGTH_SHORT)
        mySnackBar.show()
    }

    private fun deleteNote(note : Notes){
        GlobalScope.launch {
            LocalDataBase.getInstance(applicationContext).noteDao.deleteNote(note)
            launch(Dispatchers.Main) {
                getAllNotes()
                showSnackBar(getString(R.string.deletedNote))
            }
        }
    }

    private fun getAllNotes(){
        GlobalScope.launch {
            notesList = LocalDataBase.getInstance(applicationContext).noteDao.getAllNotes().toMutableList() as ArrayList<Notes>
            launch(Dispatchers.Main) {
                customAdapter?.updateNotes(notesList)
            }
        }
    }
}