package com.plcoding.instagramui.saveplace.data.contactViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.plcoding.instagramui.saveplace.data.repository.ContactRepository

@Suppress("UNCHECKED_CAST")
class ContactViewModelFactor(
    private val repository: ContactRepository
): ViewModelProvider.NewInstanceFactory() {
    override fun <T: ViewModel?> create(modelClass: Class<T>): T{
        return ContactViewModel(repository) as T
    }
}