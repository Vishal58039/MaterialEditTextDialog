package com.materialedittextdialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.text.InputFilter
import android.util.Patterns
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.custom_edit_text.view.*
import java.util.regex.Pattern

@Suppress("NAME_SHADOWING")
@SuppressLint("SetTextI18n", "InflateParams", "DefaultLocale")
class MaterialEditTextDialog(context: Context) : MaterialAlertDialogBuilder(context) {

    private var alertDialog: AlertDialog? = null

    private var hintText: String = ""
    private var text: String? = null
    private var errorMessage: String = "Unknown"
    private var inputType: Int = -1
    private var listenerPositiveBtn: (text: String, isValidated: Boolean, dialog: DialogInterface) -> Unit = { _, _, _ -> }
    private var listenerNegativeBtn: (text: String, isValidated: Boolean, dialog: DialogInterface) -> Unit = { _, _, _ -> }
    private var listenerNeutralBtn: (text: String, isValidated: Boolean, dialog: DialogInterface) -> Unit = { _, _, _ -> }
    private var maxTextLength: Int = -1
    private var validateType: Pattern? = null
    private val editTextLayoutView = LayoutInflater.from(context).inflate(R.layout.custom_edit_text, null)
    private var isLetterOrDigit = false
    init {
        setView(editTextLayoutView)
    }

    fun setMaterialEditTextHint(hintText: String): MaterialEditTextDialog {
        this.hintText = hintText
        return this@MaterialEditTextDialog
    }

    fun setOnlyLetterAndDigit(boolean: Boolean): MaterialEditTextDialog {
        this.isLetterOrDigit = boolean
        return this@MaterialEditTextDialog
    }

    fun setText(text: String?) {
        this.text = text
    }

    fun setErrorMessage(message: String) {
        this.errorMessage = message
    }

    fun setMaterialEditTextMaxLength(maxTextLength: Int): MaterialEditTextDialog {
        this.maxTextLength = maxTextLength
        return this@MaterialEditTextDialog
    }

    fun setPositiveButton(text: CharSequence?, listener: (text: String, isValidated: Boolean, dialog: DialogInterface) -> Unit): MaterialEditTextDialog {
        this.listenerPositiveBtn = listener
        return super.setPositiveButton(text, null) as MaterialEditTextDialog
    }

    fun setNegativeButton(text: CharSequence?, listener: (text: String, isValidated: Boolean, dialog: DialogInterface) -> Unit): MaterialEditTextDialog {
        this.listenerNegativeBtn = listener
        return super.setNegativeButton(text, null) as MaterialEditTextDialog
    }

    fun setValidateType(pattern: Pattern) {
        validateType = pattern
    }

    fun setInputType(type: Int) {
        this.inputType = type
    }

    fun setNeutralButton(text: CharSequence?, listener: (text: String, isValidated: Boolean, dialog: DialogInterface) -> Unit): MaterialEditTextDialog {
        this.listenerNeutralBtn = listener
        return super.setNeutralButton(text, null) as MaterialEditTextDialog
    }

    private fun dialogButtonListener() {
        alertDialog?.setOnShowListener { dialog ->
            alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
                listenerPositiveBtn.invoke(editTextLayoutView.textInput.text.toString(),
                        if (validateType != null) validate() else true, dialog)
            }
            alertDialog?.getButton(AlertDialog.BUTTON_NEGATIVE)?.setOnClickListener {
                listenerNegativeBtn.invoke(editTextLayoutView.textInput.text.toString(),
                        if (validateType != null) validate() else true, dialog)
            }
            alertDialog?.getButton(AlertDialog.BUTTON_NEUTRAL)?.setOnClickListener {
                listenerNeutralBtn.invoke(editTextLayoutView.textInput.text.toString(),
                        if (validateType != null) validate() else true, dialog)
            }
        }
    }

    override fun create(): AlertDialog {
        alertDialog = super.create()
        init(alertDialog!!)
        dialogButtonListener()
        return alertDialog!!
    }

    private fun init(dialog: AlertDialog) {
        editTextLayoutView.textInputLayout?.hint = hintText
        if (maxTextLength != -1) {
            editTextLayoutView.textInputLayout?.counterMaxLength = maxTextLength
            val fArray = arrayOfNulls<InputFilter>(1)
            fArray[0] = InputFilter.LengthFilter(maxTextLength)
            editTextLayoutView.textInput?.filters = fArray
        } else editTextLayoutView.textInputLayout?.isCounterEnabled = false

        editTextLayoutView.textInput?.doOnTextChanged { _, _, _, _ ->
            if (editTextLayoutView.textInputLayout?.error != null)
                editTextLayoutView.textInputLayout?.error = null
        }
        if (inputType != -1)
            editTextLayoutView.textInput?.inputType = inputType

        if (text != null) {
            editTextLayoutView.textInput?.setText(text)
        }
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        editTextLayoutView.textInput?.requestFocus()
    }

    private fun validate(): Boolean {
        val validateText = editTextLayoutView.textInput?.text.toString()

        if (validateType != null && validateType == Patterns.PHONE) {
            return if (validateText.length == 10 && validateType!!.matcher(validateText).matches()) {
                editTextLayoutView.textInputLayout?.error = null
                true
            } else {
                editTextLayoutView.textInputLayout?.error = errorMessage
                false
            }
        }

        return if (validateType != null && validateType!!.matcher(validateText).matches()) {
            editTextLayoutView.textInputLayout?.error = null
            true
        } else {
            editTextLayoutView.textInputLayout?.error = errorMessage
            false
        }
    }
}