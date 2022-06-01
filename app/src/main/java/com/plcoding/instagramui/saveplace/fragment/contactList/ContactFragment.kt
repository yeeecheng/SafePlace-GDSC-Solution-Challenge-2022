package com.plcoding.instagramui.saveplace.fragment.contactList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.plcoding.instagramui.saveplace.R
import com.plcoding.instagramui.saveplace.data.contactViewModel.ContactViewModel
import com.plcoding.instagramui.saveplace.data.contactViewModel.ContactViewModelFactor
import com.plcoding.instagramui.saveplace.data.db.ContactDatabase
import com.plcoding.instagramui.saveplace.data.db.entities.ContactItem
import com.plcoding.instagramui.saveplace.data.repository.ContactRepository
import com.plcoding.instagramui.saveplace.fragment.contactList.addContact.AddContactItemDialog
import com.plcoding.instagramui.saveplace.fragment.contactList.updateContact.UpdateContactItemDialog


class ContactFragment : Fragment(){

    private lateinit var database :ContactDatabase
    private lateinit var repository:ContactRepository
    private lateinit var  factor: ContactViewModelFactor
    lateinit var viewModel: ContactViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.contact_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("build","contact fragment create")
        var switch = activity?.findViewById<NavigationView>(R.id.nav_view)?.menu?.findItem(R.id.nav_light_mode)?.actionView?.findViewById<SwitchCompat>(R.id.switch_light)
        database = ContactDatabase(requireActivity().applicationContext)
        repository = ContactRepository(database)
        factor = ContactViewModelFactor(repository)
        viewModel = ViewModelProviders.of(this, factor).get(ContactViewModel::class.java)
        val adapter = ContactItemAdapter( switch,listOf(), viewModel)
        val rvContactItems:RecyclerView =view.findViewById(R.id.rv_ContactItems)

        rvContactItems.layoutManager = LinearLayoutManager(requireContext())
        rvContactItems.adapter = adapter

        adapter.setOnItemClickListener(object : ContactItemAdapter.onItemClickListener{

            override fun onItemClick(id: Int) {
                val x = UpdateContactItemDialog(requireContext(),
                    object : UpdateContactItemDialog.UpdateDialogListener {
                        override fun onUpdateButtonClicked(item: ContactItem) {
                            item.id=id
                            item.id=id
                            viewModel.update(item)
                            Toast.makeText(requireContext(),R.string.update_success,Toast.LENGTH_SHORT).show()
                        }
                    })

                x.show()
                if(activity?.findViewById<NavigationView>(R.id.nav_view)?.menu?.findItem(R.id.nav_light_mode)?.actionView?.findViewById<SwitchCompat>(R.id.switch_light)?.isChecked == true) {
                    x.window?.setBackgroundDrawableResource(R.color.white);
                    x.findViewById<EditText>(R.id.et_Name)
                        ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    x.findViewById<EditText>(R.id.et_Phone)
                        ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    x.findViewById<TextView>(R.id.tv_Title)
                        ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
                else{
                    x.window?.setBackgroundDrawableResource(R.color.grey2);
                }
            }

        })

        val rvAdd:Button =view.findViewById(R.id.rv_AddButton)

        rvAdd.setOnClickListener{
            val x = AddContactItemDialog(requireContext(),
                object : AddContactItemDialog.AddDialogListener {
                    override fun onAddButtonClicked(item: ContactItem) {
                            viewModel.upsert(item)
                    }
                })
            x.show()
            if(activity?.findViewById<NavigationView>(R.id.nav_view)?.menu?.findItem(R.id.nav_light_mode)?.actionView?.findViewById<SwitchCompat>(R.id.switch_light)?.isChecked == true) {
                x.window?.setBackgroundDrawableResource(R.color.white);
                x.findViewById<EditText>(R.id.et_Name)
                    ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                x.findViewById<EditText>(R.id.et_Phone)
                    ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                x.findViewById<TextView>(R.id.tv_Title)
                    ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }else{
                x.window?.setBackgroundDrawableResource(R.color.grey2);
            }
        }


        viewModel.getAllContactItems().observe(viewLifecycleOwner, Observer{

            adapter.items = it

            adapter.notifyDataSetChanged()//刷新資料內容

            if(adapter.itemCount==3){
                rvAdd.visibility = View.GONE
            }else{
                rvAdd.visibility = View.VISIBLE
            }
        })




    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("build","fragment destroy")
    }

}