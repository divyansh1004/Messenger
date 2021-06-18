package com.example.kotlinmessenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinmessenger.messages.ChatActivity
import com.example.kotlinmessenger.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_new_message.view.*

class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title = "Select User"
        /*val adapter = GroupAdapter<GroupieViewHolder>()
        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())
        recycleview_newmessage.adapter = adapter*/
        fetchuser()

    }
    companion object{
        val USER_KEY="USER_KEY"
    }

    private fun fetchuser() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            //p0 contains all the data
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                p0.children.forEach {
                    Log.d("NewMessage", it.toString())
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        adapter.add(UserItem(user))
                    }

                }
                adapter.setOnItemClickListener { item, view ->
                    val useritem=item as UserItem
                   val intent= Intent(view.context,ChatActivity::class.java)

                    //intent.putExtra(USER_KEY,useritem.user.username)
                    intent.putExtra(USER_KEY,useritem.user)
                    //a parcel to carry out data
                    startActivity(intent)
                    finish()

                }
                recycler_messages.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
}

class UserItem(val user: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.username_text_new.text=user.username
        Picasso.get().load(user.profileimageurl).into(viewHolder.itemView.imageViewage_new_message)
    }

    override fun getLayout(): Int {
        return R.layout.user_new_message
    }
}