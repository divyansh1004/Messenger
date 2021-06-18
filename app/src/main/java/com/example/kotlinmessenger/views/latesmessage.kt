package com.example.kotlinmessenger.views

import android.util.Log
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.models.ChatMessage
import com.example.kotlinmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.lates_message_homepage.view.*


    class latesmessage(val chatMessage: ChatMessage) : com.xwray.groupie.Item<GroupieViewHolder>() {
        var chatpartneruser:User?=null
        override fun getLayout(): Int {
            return R.layout.lates_message_homepage
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            Log.d("hi", "")
            viewHolder.itemView.textview_latestmessgaes.text = chatMessage.text
            val chatpartnerid: String
            if (chatMessage.fromid == FirebaseAuth.getInstance().uid) {
                chatpartnerid = chatMessage.toid
            } else {
                chatpartnerid = chatMessage.fromid
            }
            val ref = FirebaseDatabase.getInstance().getReference("/users/$chatpartnerid")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatpartneruser = snapshot.getValue(User::class.java)
                    viewHolder.itemView.textview_latestusername.text = chatpartneruser?.username
                    val targetimage = viewHolder.itemView.imageView_latestmessage
                    Picasso.get().load(chatpartneruser?.profileimageurl).into(targetimage)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })


        }
    }
