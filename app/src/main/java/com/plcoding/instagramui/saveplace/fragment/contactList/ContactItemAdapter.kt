package com.plcoding.instagramui.saveplace.fragment.contactList

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.plcoding.instagramui.saveplace.*
import com.plcoding.instagramui.saveplace.data.contactViewModel.ContactViewModel
import com.plcoding.instagramui.saveplace.data.db.entities.ContactItem
import com.plcoding.instagramui.saveplace.mainActivity.MainActivity

class ContactItemAdapter(

    switch: SwitchCompat?,
    var items: List<ContactItem>,
    private val viewModel: ContactViewModel
): RecyclerView.Adapter<ContactItemAdapter.ContactViewHolder>() {

    private var Switch =switch

    private lateinit var  mListener: onItemClickListener
    var mode: Boolean = false

    interface onItemClickListener{

        fun onItemClick(id:Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)

        return ContactViewHolder(view,mListener)
    }


    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {

        mode =  Switch!!.isChecked ==true

        val curContactItem = items[position]
        val tvId =holder.itemView.findViewById<TextView>(R.id.tv_Id)
        val tvName =holder.itemView.findViewById<TextView>(R.id.tv_Name)
        val tvPhone =holder.itemView.findViewById<TextView>(R.id.tv_Phone)
        val ivDelete =holder.itemView.findViewById<ImageView>(R.id.iv_Delete)


        tvId.text = (position+1).toString()
        tvName.text = curContactItem.name
        tvPhone.text = curContactItem.phoneNumber
        ivDelete.setOnClickListener{
            viewModel.delete(curContactItem)
        }


        if(mode) {
            tvName?.setTextColor(Color.parseColor("#000000"))
            tvPhone?.setTextColor(Color.parseColor("#000000"))
            ivDelete?.setColorFilter(Color.parseColor("#888888"))
        }else{
            tvName?.setTextColor(Color.parseColor("#ffffff"))
            tvPhone?.setTextColor(Color.parseColor("#ffffff"))
            ivDelete?.setColorFilter(Color.parseColor("#ffffff"))
        }



    }




    override fun getItemCount(): Int {
        return items.size
    }




    inner class ContactViewHolder(itemView: View,listener: ContactItemAdapter.onItemClickListener): RecyclerView.ViewHolder(itemView){
        private val contactName: TextView = itemView.findViewById(R.id.tv_Name)

        init{

            itemView.setOnClickListener{
                val name =contactName.text.toString()
                val contactId =viewModel.getIdByName(name)
                listener.onItemClick(contactId)

            }
        }
    }
}
