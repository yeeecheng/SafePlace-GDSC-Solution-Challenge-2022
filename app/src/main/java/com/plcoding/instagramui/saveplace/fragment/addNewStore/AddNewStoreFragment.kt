package com.plcoding.instagramui.saveplace.fragment.addNewStore

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.plcoding.instagramui.saveplace.R
import com.plcoding.instagramui.saveplace.mainActivity.clientSocket


class AddNewStoreFragment : Fragment(){

    private var ip="35.206.214.161"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_a_store_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etStoreName: EditText = view.findViewById(R.id.et_store_name)
        val etStoreInformation: EditText = view.findViewById(R.id.et_store_information)
        val etStoreAddress: EditText = view.findViewById(R.id.et_store_address)
        val storeKind: Spinner =view.findViewById(R.id.spinner_store_kind)
        val openTime: Spinner = view.findViewById(R.id.spinner_store_open_time)
        val closeTime: Spinner = view.findViewById(R.id.spinner_store_close_time)
        val rvReport: Button = view.findViewById(R.id.rv_report_button)
        var storeKinds: String = ""
        var storeOpeningTime: Int = -1
        var storeClosingTime: Int = -1

        val storeKindArray = resources.getStringArray(R.array.store)
        val openTimeArray = resources.getStringArray(R.array.open_time)
        val closeTimeArray = resources.getStringArray(R.array.close_time)
        val store= arrayListOf<String>(*storeKindArray)
        val openingtime= arrayListOf<String>(*openTimeArray)
        val closingtime= arrayListOf<String>(*closeTimeArray)



        var switch = activity?.findViewById<NavigationView>(R.id.nav_view)?.menu?.findItem(R.id.nav_light_mode)?.actionView?.findViewById<SwitchCompat>(R.id.switch_light)?.isChecked
        if(switch == true){
            etStoreName.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            etStoreInformation.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            etStoreAddress.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }
        else{
            etStoreName.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            etStoreInformation.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            etStoreAddress.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }


        // STORE KIND
        val spinnerStoreKindArrayAdapter: ArrayAdapter<String> = object: ArrayAdapter<String>(
            requireActivity().applicationContext, R.layout.spinner_item, store)  {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(
                position: Int, convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY)
                } else {
//                    tv.setTextColor(Color.BLACK)
                }
                return view
            }
        }
        spinnerStoreKindArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        storeKind.setAdapter(spinnerStoreKindArrayAdapter)

        storeKind.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    storeKinds = store[position]
                    println(store[position])
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })





        //OPEN TIME ADAPTER
        val spinnerOpenTimeArrayAdapter: ArrayAdapter<String> = object: ArrayAdapter<String>(
            requireActivity().applicationContext, R.layout.spinner_item, openingtime)  {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(
                position: Int, convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY)
                }
                return view
            }
        }
        spinnerOpenTimeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)



        openTime.adapter = spinnerOpenTimeArrayAdapter
        openTime.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position > 0) {
                    storeOpeningTime = openingtime[position].toInt()
                    println(openingtime[position])
                }
            }

        }






        //CLOSE TIME ADAPTER
        val spinnerCloseTimeArrayAdapter: ArrayAdapter<String> = object: ArrayAdapter<String>(
            requireActivity().applicationContext, R.layout.spinner_item, closingtime)  {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(
                position: Int, convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY)
                }
                return view
            }
        }
        spinnerCloseTimeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        closeTime.adapter = spinnerCloseTimeArrayAdapter
        closeTime.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position > 0) {
                    storeClosingTime = closingtime[position].toInt()
                    println(closingtime[position].toInt())
                }
            }

        }

        rvReport.setOnClickListener{

            var storeName = etStoreName?.text.toString()
            var storeInformation = etStoreInformation?.text.toString()
            var storeAddress = etStoreAddress?.text.toString()

            val characters: String = "'.,:;*?~`!@#\$%^&<>{}[]\\|/"

            if(storeName.isEmpty() || storeAddress.isEmpty() || storeKinds.isEmpty() || storeOpeningTime == -1 || storeClosingTime == -1){
                Toast.makeText(context, R.string.enter_all_info, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }



            if(filterString(storeName) || filterString(storeInformation) || filterString(storeAddress)){
                Toast.makeText(context, R.string.enter_wrong_type_info, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("data3","click@@@@!!!")
            Thread{
                Log.d("data3","click!!!")
                var client = clientSocket(ip,3333)
                client.initConnect()

                client.addNewStore(storeName,kindChange(storeKinds),storeInformation,storeAddress,storeOpeningTime,storeClosingTime)
                var msg = client.receiveMessage()
                client.closeConnect()
                Log.d("mmm",msg)
                val mHandler = Handler(Looper.getMainLooper())

                mHandler.post {
                    if(msg=="Success"){
                        Toast.makeText(context,R.string.add_store_send_success,Toast.LENGTH_SHORT).show()
                        etStoreName.setText("")
                        etStoreInformation.setText("")
                        etStoreAddress.setText("")
                        storeKind.setSelection(0)
                        storeKinds=""
                        openTime.setSelection(0)
                        storeOpeningTime=-1
                        closeTime.setSelection(0)
                        storeClosingTime=-1
                    }
                    else {
                        mHandler.post {
                            Toast.makeText(context,R.string.add_store_send_failed,Toast.LENGTH_SHORT).show()
                        }
                    }
                }


            }.start()

        }

    }

    private fun kindChange(kind:String):String{
        var shortHand=""
        when(kind){
            "Convience Store" ->shortHand="CS"
            "Gas Station" ->shortHand="GS"
            "Police Station" ->shortHand="PS"
            "Fire Department" ->shortHand="FD"
            "Hospital" ->shortHand="H"
            "Restaurant"->shortHand="R"
            "Other" ->shortHand="O"

        }
        return shortHand
    }


    private fun filterString(s: String): Boolean{
        val characters: String = "'.,:;*?~`!@#\$%^&<>{}[]\\|/"

        s.forEach {
            if(characters.contains(it, ignoreCase = true)) return true
        }
        return false;
    }


}

