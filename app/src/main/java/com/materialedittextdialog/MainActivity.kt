package com.materialedittextdialog

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clickMe.setOnClickListener {
            val editTextDialog = MaterialEditTextDialog(this)
            editTextDialog.apply {
                setErrorMessage("Email is not valid")
                setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                setValidateType(Patterns.EMAIL_ADDRESS)
                setMaterialEditTextHint("Email ID")
                setPositiveButton("Submit"){text, isValidated, dialog ->
                    textView.text = "$text \n isValid: $isValidated"
                    if(isValidated) {
                        dialog.dismiss() // manually dismiss dialog is important
                    }
                }
                setNegativeButton("Cancel"){text,isValidated, dialog ->
                    dialog.dismiss() // manually dismiss dialog is important
                }
            }
            editTextDialog.show()
        }

    }
}