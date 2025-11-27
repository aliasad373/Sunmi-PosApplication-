package com.mindcrafts.posapp

import android.text.method.PasswordTransformationMethod
import android.view.View

class AsteriskPasswordTransformation : PasswordTransformationMethod() {
    override fun getTransformation(source: CharSequence?, view: View?): CharSequence {
        return object : CharSequence {
            override val length: Int get() = source?.length ?: 0
            override fun get(index: Int): Char = '*'
            override fun subSequence(startIndex: Int, endIndex: Int): CharSequence =
                "*".repeat(endIndex - startIndex)
        }
    }
}