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

class DialogExport : DialogFragment() {

    private lateinit var listener: DialogExportListener

    interface DialogExportListener {
        fun onDialogPositiveClick(dialog: DialogExport, strPath: String)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as DialogExportListener
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
        dialog?.setTitle(inflater.context.getString(R.string.export))
        val view = inflater.inflate(R.layout.dialog_export, null)
        val editText = view.findViewById(R.id.edtExportPath) as EditText

        val btnOk = view.findViewById(R.id.btnOk) as Button
        btnOk.setOnClickListener { listener.onDialogPositiveClick(this, editText.text.toString()) }

        val btnCancel = view.findViewById(R.id.btnCancel) as Button
        btnCancel.setOnClickListener { dialog?.dismiss() }

        return view
    }


}