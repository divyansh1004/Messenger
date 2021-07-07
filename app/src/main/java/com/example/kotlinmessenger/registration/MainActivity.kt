package com.example.kotlinmessenger

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinmessenger.messages.MessagesActivity
import com.example.kotlinmessenger.models.User
import com.example.kotlinmessenger.registration.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        register_btn_registration.setOnClickListener {
            performRegister()
        }

        already_register.setOnClickListener {
            Log.d("MainActivity", "Try to show login activity")
            //launch login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }
        //for imaages
        image_btn.setOnClickListener {
            Log.d("Main", "Photo selector")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            @Suppress("DEPRECATION")
            startActivityForResult(intent, 0)

        }


    }

    var selectedphoto: Uri? = null

    //after selecting from gallery
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("Register", "photo was selected")
            selectedphoto = data?.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedphoto)
            selectimage_register.setImageBitmap(bitmap)
            image_btn.alpha = 0f

            Log.d("Register", "photo was displayed")
            //3 13:00

        }
    }
//to perform registration
    private fun performRegister() {
        val email = email_register.text.toString()
        val password = password_register.text.toString()

        Log.d("MainActivity", "Email is= " + email)
        Log.d("MainActivity", "Password : $password")
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter above details", Toast.LENGTH_SHORT).show()
            return
        }
        //firebase authentication
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d(
                    "Main",
                    "Email and password= ${email} and $password and uid= ${it.result?.user?.uid}"
                )
                uploadimage()

            }
            .addOnFailureListener {
                Toast.makeText(this, "\"Failed to create ${it.message}", Toast.LENGTH_SHORT).show()
                Log.d("Main", "Failed to create ${it.message}")
            }
    }
//for uploading image to firestore
    private fun uploadimage() {
        if (selectedphoto == null)
            return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedphoto!!)
            .addOnSuccessListener {
                Log.d("upload", "successfully upload images = ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    it.toString()
                    Log.d("Register", "File location  = ${it}")

                    saveusertofirebasedatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d("Register", "Failed to upload ${it.message}")
            }
    }

    //saving data
    private fun saveusertofirebasedatabase(profileimageurl: String) {

        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, username_register.text.toString(), profileimageurl)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("Registration", "saved to firebase database")
                val intent = Intent(this, MessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)//backs to the desktop
                startActivity(intent)

            }
            .addOnFailureListener {
                Log.d("Registerter", "Failed to save ${it.message}")
            }
    }
}

