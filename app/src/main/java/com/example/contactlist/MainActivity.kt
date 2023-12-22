package com.example.contactlist

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.Manifest
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val READ_CONTACTS_PERMISSION_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val contact: RecyclerView = findViewById(R.id.contact_list)
        contact.layoutManager = LinearLayoutManager(this)

        val btn: Button = findViewById(R.id.btn_read_contact)
        btn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                ReadContactsTask(this).execute()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    READ_CONTACTS_PERMISSION_REQUEST
                )
            }
        }
    }

    class ContactAdapter(private val items: List<ContactDTO>, private val context: Context) :
        RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.name.text = items[position].name
            holder.number.text = items[position].number
            if (items[position].image != null)
                holder.profile.setImageBitmap(items[position].image)
            else
                holder.profile.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_launcher_round))
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(context).inflate(R.layout.contact_child, parent, false))
        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val name = v.findViewById<TextView>(R.id.tv_name)
            val number = v.findViewById<TextView>(R.id.tv_number)
            val profile = v.findViewById<ImageView>(R.id.iv_profile)
        }
    }

    class ReadContactsTask(private val activity: MainActivity) : AsyncTask<Void, Void, List<ContactDTO>>() {

        @SuppressLint("Range")
        override fun doInBackground(vararg params: Void?): List<ContactDTO> {
            val contactList: MutableList<ContactDTO> = ArrayList()
            val contacts = activity.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
            )
            if (contacts != null) {
                while (contacts.moveToNext()) {
                    val name =
                        contacts?.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val number =
                        contacts?.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    val obj = ContactDTO()
                    if (name != null) {
                        obj.name = name
                    }
                    if (number != null) {
                        obj.number = number
                    }

                    val photo_uri =
                        contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
                    if (photo_uri != null) {
                        obj.image =
                            MediaStore.Images.Media.getBitmap(activity.contentResolver, Uri.parse(photo_uri))
                    }
                    contactList.add(obj)
                }
                contacts.close()
            }
            return contactList
        }

        override fun onPostExecute(result: List<ContactDTO>) {
            activity.findViewById<RecyclerView>(R.id.contact_list).adapter = ContactAdapter(result, activity)
        }
    }
}

//class MainActivity : AppCompatActivity() {
//
//    @SuppressLint("Range")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        val contact : RecyclerView
//        contact = findViewById(R.id.contact_list)
//
//        contact.layoutManager = LinearLayoutManager(this)
//
//        val btn : Button
//        btn = findViewById(R.id.btn_read_contact)
//
//        btn.setOnClickListener {
//            //We need to verify permission
//            val contactList : MutableList<ContactDTO> = ArrayList()
//            val contacts = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null)
//            if (contacts != null) {
//                while (contacts.moveToNext()){
//                    val name = contacts?.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
//                    val number = contacts?.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
//                    val obj = ContactDTO()
//                    if (name != null) {
//                        obj.name = name
//                    }
//                    if (number != null) {
//                        obj.number = number
//                    }
//
//                    val photo_uri = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
//                    if(photo_uri != null){
//                        obj.image = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(photo_uri))
//                    }
//                    contactList.add(obj)
//                }
//            }
//            contact.adapter = ContactAdapter(contactList,this)
//            if (contacts != null) {
//                contacts.close()
//            }
//        }
//
//    }
//
//    class ContactAdapter(items : List<ContactDTO>,ctx: Context) : RecyclerView.Adapter<ContactAdapter.ViewHolder>(){
//
//        private var list = items
//        private var context = ctx
//
//        override fun getItemCount(): Int {
//            return list.size
//        }
//
//        override fun onBindViewHolder(holder: ContactAdapter.ViewHolder, position: Int) {
//            holder.name.text = list[position].name
//            holder.number.text = list[position].number
//            if(list[position].image != null)
//                holder.profile.setImageBitmap(list[position].image)
//            else
//                holder.profile.setImageDrawable(ContextCompat.getDrawable(context,R.mipmap.ic_launcher_round))
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactAdapter.ViewHolder {
//            return ViewHolder(LayoutInflater.from(context).inflate(R.layout.contact_child,parent,false))
//        }
//
//
//        class ViewHolder(v: View) : RecyclerView.ViewHolder(v){
//            val name = v.findViewById<TextView>(R.id.tv_name)
//            val number = v.findViewById<TextView>(R.id.tv_number)
//            val profile = v.findViewById<ImageView>(R.id.iv_profile)
//        }
//    }
//
//}