package com.example.kotlinmessenger.messages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.kotlinmessenger.MainActivity
import com.example.kotlinmessenger.NewMessageActivity
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.models.ChatMessage
import com.example.kotlinmessenger.models.User
import com.example.kotlinmessenger.views.latesmessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_messages.*

class MessagesActivity : AppCompatActivity() {
    //global variable type
    companion object {
        var currentuser: User? = null
        val TAG = "LatestMessages"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        recycler_messages_homepage.adapter = adapter
        recycler_messages_homepage.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        //set item click to move to chat
        adapter.setOnItemClickListener { item, view ->
            Log.d(TAG, "122332")
            val intent = Intent(this, ChatActivity::class.java)
            val row = item as latesmessage//safe casting
            intent.putExtra(NewMessageActivity.USER_KEY, row.chatpartneruser)
            startActivity(intent)
        }
        //setupdumy()
        listenlatestmessage()
        fetchuser()
        verifyication()

    }


    /*private fun setupdumy() {

        adapter.add(latesmessage())

    }*/
    val latestmessagemap = HashMap<String, ChatMessage>()
    private fun refreshrecyclerviewmessage() {
        adapter.clear()
        latestmessagemap.values.forEach {
            adapter.add(latesmessage(it))
        }
    }

    private fun listenlatestmessage() {
        val fromid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest_messages/$fromid")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                Log.d("Why", "Idont know $chatMessage")
                latestmessagemap[p0.key!!] = chatMessage
                refreshrecyclerviewmessage()

            }

            //
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                Log.d("Why is this", "Idont know $chatMessage")
                latestmessagemap[p0.key!!] = chatMessage
                refreshrecyclerviewmessage()

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    val adapter = GroupAdapter<GroupieViewHolder>()
    private fun fetchuser() {

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                currentuser = p0.getValue(User::class.java)
                Log.d("latest messsage", "Current user=${currentuser?.username}")
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun verifyication() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)//backs to the desktop
            startActivity(intent)

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.signout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)//backs to the desktop
                startActivity(intent)

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}