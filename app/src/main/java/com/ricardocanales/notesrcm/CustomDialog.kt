package com.ricardocanales.notesrcm

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.lang.IllegalStateException

class CustomDialog(private val message : String, private val listener: DialogInterface.OnClickListener): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(message)
            builder.setPositiveButton(getString(R.string.btn_ace_dialog), listener)
            builder.setNegativeButton(getString(R.string.btn_can_dialog)){_,_ ->}
            builder.create()
        } ?: throw IllegalStateException("Activity is null")
    }
}