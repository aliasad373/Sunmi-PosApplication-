package com.mindcrafts.posapp.view


import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.mindcrafts.posapp.R

class LoadingDialogNew(private val context: Context) {
    private var dialog: Dialog? = null

    fun show(message: String = "Loading...", cancelable: Boolean = false) {
        if (dialog?.isShowing == true) return

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_loading, null)

        view.findViewById<TextView>(R.id.tv_message).text = message

        dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(view)
            setCancelable(cancelable)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            show()
        }
    }

    fun dismiss() {
        dialog?.let {
            if (it.isShowing) it.dismiss()
        }
        dialog = null
    }
}
