package com.example.instagramclone

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage




class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage


    var userEmail: EditText? = null
    var userPassword: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        userEmail = findViewById(R.id.userEmail)
        userPassword = findViewById(R.id.userPassword)

        if (auth.currentUser != null) {
            logIn()
        }
    }




    fun logInClicked(view: View){
        //check if we can login the user
        auth.signInWithEmailAndPassword(userEmail?.text.toString(), userPassword?.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                       logIn()
                        Toast.makeText(this, "Login successful, welcome ", Toast.LENGTH_LONG ).show()
                    } else {
                      //sign up user
                      auth.createUserWithEmailAndPassword(userEmail?.text.toString(), userPassword?.text.toString()).addOnCompleteListener(this ){ task ->
                          if (task.isSuccessful) {
                              //add the new person to the database

                              FirebaseDatabase.getInstance().getReference().child("Users").child(task.result!!.user!!.uid).child("email").setValue(userEmail?.text.toString())

                              logIn()
                              Toast.makeText(this, "Account created", Toast.LENGTH_LONG ).show()
                          } else {
                              Toast.makeText(this, "Login Failed. Please try again", Toast.LENGTH_LONG ).show()
                          }
                      }
                    }
                }


    }

    fun logIn() {
    //Move to next activity
      val nextScreen = Intent(this, SnapActivity::class.java)
       startActivity(nextScreen)
        // set current user to back null
    }

}

