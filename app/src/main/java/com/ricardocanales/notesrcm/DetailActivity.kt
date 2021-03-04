package com.ricardocanales.notesrcm

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaScannerConnection
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.widget.ImageView
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import com.ricardocanales.notesrcm.database.LocalDataBase
import com.ricardocanales.notesrcm.model.Notes
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity() {

    private val requestGalleryId = 1
    private val requestCameraId = 2
    var photoFile : File? = null

    lateinit var photoImageView: ImageView
    lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        photoImageView = findViewById(R.id.photoImageView)
        try {

            if(MainActivity.indicator == 0){
                fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
                title_note_edit_text.text = "".toEditable()
                resume_note_detail_edit_text.text = "".toEditable()
                importance_switch.isChecked = false
                //photoImageView = findViewById(R.id.photoImageView)
            }

            if(MainActivity.indicator == 1){
                val note = intent.getSerializableExtra("noteSelected") as Notes
                if(note.noteTitle != "") {
                    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
                    title_note_edit_text.text = note.noteTitle.toEditable()
                    resume_note_detail_edit_text.text = note.noteResume.toEditable()
                    importance_switch.isChecked = note.noteImportance
                    //photoImageView = note.noteImage
                }
            }

            save_note_button.setOnClickListener() {
                if(MainActivity.indicator == 0){
                    saveNewNote()
                }
                if(MainActivity.indicator == 1) {
                    val note = intent.getSerializableExtra("noteSelected") as Notes
                    if (note.noteTitle != "") {
                        saveOldNote(note)
                    }
                }
            }

            take_photo.setOnClickListener(){
                dispatchTakePhotoIntent()
            }

            get_from_gallery.setOnClickListener(){
                chooseImageGallery()
            }

        } catch (e: Exception) {
            Log.d("error", "" + e)
        }

    }

    private fun insertNote(note: Notes) {
        GlobalScope.launch {
            LocalDataBase.getInstance(applicationContext).noteDao.insertNote(note)
            showSnackBar(getString(R.string.addedNote))
        }
    }

    private fun updateNote(note: Notes) {
        GlobalScope.launch {
            LocalDataBase.getInstance(applicationContext).noteDao.updateNote(note)
            showSnackBar(getString(R.string.updatedNote))
        }
    }

     fun showSnackBar(message: String) {
        val mySnackBar =
            Snackbar.make(findViewById(R.id.my_coordinator2), message, Snackbar.LENGTH_SHORT)
        mySnackBar.show()
    }

     fun saveNewNote() {
        try {
            val noteTitle = title_note_edit_text.text.toString()
            val noteResume = resume_note_detail_edit_text.text.toString()
            val isImportant = importance_switch.isChecked
            //val noteImage =  R.id.photoImageView

            if (noteTitle != "" && noteResume != "") {
                val newNote = Notes(noteTitle, isImportant, noteResume)
                insertNote(newNote)
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                showSnackBar(getString(R.string.completeData))
            }

        } catch (e: Exception) {
            Log.d("Tag", "" + e)
        }
    }

     fun saveOldNote(note: Notes) {
        try {
            val noteTitle = title_note_edit_text.text.toString()
            val noteResume = resume_note_detail_edit_text.text.toString()
            val isImportant = importance_switch.isChecked
            val noteId = note.noteId
            //val noteImage = note.noteImage

            if (noteTitle != "" && noteResume != "") {
                if (noteId == note.noteId) {
                    val updateNote = Notes(noteTitle, isImportant, noteResume, noteId = noteId)
                    updateNote(updateNote)
                }
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                showSnackBar(getString(R.string.completeData))
            }

        } catch (e: Exception) {
            Log.d("error", "" + e)
        }
    }

    private fun dispatchTakePhotoIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
                takePictureIntent ->
            photoFile = try {
                createImageFile()
            } catch (ex: IOException) {
                null
            }

            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(this,BuildConfig.APPLICATION_ID+".provider",it) //Mainfest
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI)
                startActivityForResult(takePictureIntent,requestCameraId)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File{
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_",".jpg",storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun chooseImageGallery() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            startActivityForResult(it, requestGalleryId)
        }
    }

    private fun addPictureToGallery(){ //Funciona de Android 9 hacia abajo...
        val file = File(currentPhotoPath)
        MediaScannerConnection.scanFile(this, arrayOf(file.toString()),null,null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == requestGalleryId && resultCode == Activity.RESULT_OK) {
            photoImageView.setImageURI(data?.data)
        } else if(requestCode==requestCameraId && resultCode == Activity.RESULT_OK){
            val photoTakenBitmap = BitmapFactory.decodeFile(photoFile?.absolutePath)
            photoImageView.setImageBitmap(rotateImage(photoTakenBitmap,90))
            addPictureToGallery()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun rotateImage(img: Bitmap, degree: Int): Bitmap?{
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedImage = Bitmap.createBitmap(img,0,0,img.width,img.height,matrix,true)
        img.recycle()
        return rotatedImage
    }

}



