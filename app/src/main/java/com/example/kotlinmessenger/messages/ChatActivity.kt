package com.example.kotlinmessenger.messages

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinmessenger.NewMessageActivity
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.models.ChatMessage
import com.example.kotlinmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.chat_message_from.view.*
import kotlinx.android.synthetic.main.chat_message_to.view.*

class ChatActivity : AppCompatActivity() {
    companion object {
        const val TAG = " CHATLOG "

    }

    val adapter = GroupAdapter<GroupieViewHolder>()
    var touser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        recyclyer_chat.adapter = adapter
        touser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = touser?.username


        // setupdumydata()
        listneformessage()


        senbtn_chat.setOnClickListener {
            Log.d(TAG, "Atemp to send message")
            performsendmessage()
        }

    }

    private fun listneformessage() {
        //fetching data from firebase

        val fromid = FirebaseAuth.getInstance().uid
        val toid = touser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user_messages/$fromid/$toid")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatmmess = p0.getValue(ChatMessage::class.java)
                if (chatmmess != null) {
                    Log.d(TAG, "here=${chatmmess.text}")

                    if (chatmmess.fromid == FirebaseAuth.getInstance().uid) {
                        Log.d(TAG, "Whattt")
                        val currentuser = MessagesActivity.currentuser ?: return
                        Log.d(TAG, "Whattt22")
                        adapter.add(ChatfromItem(chatmmess.text, currentuser))
                    } else {

                        adapter.add(ChattoItem(chatmmess.text, touser!!))
                    }


                }
                recyclyer_chat.scrollToPosition(adapter.itemCount-1)


            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


    }


    private fun performsendmessage() {
        val text = edittex_chat.text.toString()
        val fromid = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toid = user?.uid
        if (fromid == null) return
        //val ref = FirebaseDatabase.getInstance().getReference("/messages").push()
        val ref =
            FirebaseDatabase.getInstance().getReference("/user_messages/$fromid/$toid").push()
        val refto =
            FirebaseDatabase.getInstance().getReference("/user_messages/$toid/$fromid").push()


        val chatmessage =
            ChatMessage(ref.key!!, text, fromid, toid!!, System.currentTimeMillis() / 1000)


        ref.setValue(chatmessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved out message ${ref.key}")
                edittex_chat.text.clear()
                recyclyer_chat.scrollToPosition(adapter.itemCount - 1)

            }
        refto.setValue(chatmessage)
        val refltest =
            FirebaseDatabase.getInstance().getReference("/latest_messages/$fromid/$toid")
        refltest.setValue(chatmessage)
        val refltestto =
            FirebaseDatabase.getInstance().getReference("/latest_messages/$toid/$fromid")
        refltestto.setValue(chatmessage)

    }


}


class ChatfromItem(val text: String, val user: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_from_chat.text=text
        val uri = user.profileimageurl
        val targetimage = viewHolder.itemView.imageView_from
        Picasso.get().load(uri).into(targetimage)

    }

    override fun getLayout(): Int {
        return R.layout.chat_message_from
    }
}

class ChattoItem(val text: String, val user: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_to_chat.text = text

        //image load
        val uri = user.profileimageurl
        val targetimage = viewHolder.itemView.imageView_to_chat_to
        Picasso.get().load(uri).into(targetimage)
    }

    override fun getLayout(): Int {
        return R.layout.chat_message_to
    }
}
