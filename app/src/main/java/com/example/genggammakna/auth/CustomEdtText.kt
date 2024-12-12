package com.example.genggammakna.auth

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import com.example.genggammakna.R


class EdEdtEmail : AppCompatEditText {
    constructor(context: Context) : super(context) {
        initialized()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialized()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialized()
    }

    private fun initialized() {
        hint = context.getString(R.string.enter_your_email)
        addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                error = if (!Patterns.EMAIL_ADDRESS.matcher(s).matches() && s.isNotEmpty()) {
                    context.getString(R.string.invalid_email_address)
                } else {
                    null
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
class EdtTextPassword : AppCompatEditText {
    constructor(context: Context) : super(context) {
        initialized()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialized()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialized()
    }

    private fun initialized() {
        hint = context.getString(R.string.enter_your_password)
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                error = if (s.isNullOrEmpty() || s.length < 8) {
                    context.getString(R.string.password_must_be_at_least_8_characters)
                } else {
                    null
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}


class EditTextConfirmPassword : AppCompatEditText {
    private var password: EdtTextPassword? = null

    constructor(context: Context) : super(context) {
        initialized()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialized()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialized()
    }

    private fun initialized() {
        hint = context.getString(R.string.confirm_your_password)
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                error = when {
                    s.isNullOrEmpty() || s.length < 8 -> {
                        context.getString(R.string.password_must_be_at_least_8_characters)
                    }
                    password != null && password?.text.toString() != s.toString() -> {
                        context.getString(R.string.passwords_do_not_match)
                    }
                    else -> {
                        null
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}


