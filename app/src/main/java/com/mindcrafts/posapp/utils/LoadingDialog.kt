package com.mindcrafts.posapp.utils

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.Window
import com.mindcrafts.posapp.R

private var loadingDialog: Dialog? = null


object LoadingDialog {

    @JvmStatic
    fun Activity.showLoading(message: String = "Loading...") {
        if (loadingDialog?.isShowing == true) return

        loadingDialog = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(
                LayoutInflater.from(this@showLoading).inflate(R.layout.custom_loading_dialog, null)
            )

            // Set text dynamically
            findViewById<android.widget.TextView>(R.id.tvLoadingText)?.text = message

            window?.setBackgroundDrawableResource(android.R.color.transparent)
            show()
        }
    }

    @JvmStatic
    fun Activity.hideLoading() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }
}
