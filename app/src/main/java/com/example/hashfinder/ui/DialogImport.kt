package com.example.hashfinder.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.hashfinder.R
import java.lang.ClassCastException

class DialogImport : DialogFragment() {

    private lateinit var listener: DialogImportListener

    interface DialogImportListener {
        fun onDialogPositiveClick(dialog: DialogImport, strPath: String)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as DialogImportListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                (context.toString() +
                        " must implement listener")
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.setTitle(inflater.context.getString(R.string.importStr))
        val view = inflater.inflate(R.layout.dialog_import, null)
        val editText = view.findViewById(R.id.edtImportPath) as EditText

        val btn = view.findViewById(R.id.btnOk) as Button
        btn.setOnClickListener { listener.onDialogPositiveClick(this, editText.text.toString()) }

        val btnCancel = view.findViewById(R.id.btnCancel) as Button
        btnCancel.setOnClickListener { dialog?.dismiss() }

        return view
    }


}