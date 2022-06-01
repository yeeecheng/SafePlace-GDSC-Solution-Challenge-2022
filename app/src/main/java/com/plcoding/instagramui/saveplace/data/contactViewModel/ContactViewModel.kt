package com.plcoding.instagramui.saveplace.data.contactViewModel

import androidx.lifecycle.ViewModel
import com.plcoding.instagramui.saveplace.data.db.entities.ContactItem
import com.plcoding.instagramui.saveplace.data.repository.ContactRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactViewModel(
    private val repository: ContactRepository
) : ViewModel() {

    fun upsert(item: ContactItem) = CoroutineScope(Dispatchers.Main).launch {
        repository.upsert(item)
    }

    fun delete(item: ContactItem) = CoroutineScope(Dispatchers.Main).launch {
        repository.delete(item)
    }

    fun update(item: ContactItem) = CoroutineScope(Dispatchers.Main).launch {
        repository.update(item)
    }

    fun getAllContactItems() = repository.getAllContactItems()

    fun getIdByName (name :String):Int = repository.getIdByName(name)

    fun getContactPhoneNumber()=repository.getContactPhoneNumber()

    fun getItemNameById(id :Int)=repository.getItemNameById(id)

    fun getItemPhoneNumberById(id:Int)=repository.getItemPhoneNumberById(id)
}