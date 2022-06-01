package com.plcoding.instagramui.saveplace.fragment.contactList.updateContact

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import com.plcoding.instagramui.saveplace.R
import com.plcoding.instagramui.saveplace.data.db.entities.ContactItem

class UpdateContactItemDialog(context: Context, var updateDialogListener: UpdateDialogListener): AppCompatDialog(context) {

    interface UpdateDialogListener {
        fun onUpdateButtonClicked(item: ContactItem)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_edit_contact_item)

        //etName.setText(list)
        val tvAdd : TextView? = findViewById(R.id.tv_Add)
        val tvCancel : TextView? =findViewById(R.id.tv_Cancel)
        val etName : EditText? =findViewById(R.id.et_Name)
        val etPhone:EditText?=findViewById(R.id.et_Phone)

        tvAdd?.setOnClickListener{
            val name = etName?.text.toString()
            val phone = etPhone?.text.toString()

            if(name.isEmpty() || phone.isEmpty()){
                Toast.makeText(context, R.string.enter_all_info, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(phone.length!=10){
                Toast.makeText(context, R.string.enter_right_phone_number, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val item = ContactItem(null,name, phone)
            updateDialogListener.onUpdateButtonClicked(item)
            dismiss()
        }

        tvCancel?.setOnClickListener{
            cancel()
        }
    }
}