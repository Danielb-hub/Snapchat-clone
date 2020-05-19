package com.example.instagramclone

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class ViewSnapActivity : AppCompatActivity() {
    var messageTextView: TextView? = null
    var snapImageView: ImageView? = null
    val auth = FirebaseAuth.getInstance()
    var imageReference: StorageReference? = null
    var fileRef : StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_snap)
        messageTextView = findViewById(R.id.messageTextView)
        snapImageView = findViewById(R.id.snapImageView)
        imageReference = FirebaseStorage.getInstance().reference.child("images")
        messageTextView?.text = intent.getStringExtra("message")
        fileRef = imageReference!!.child(intent.getStringExtra("imageName"))
        Log.e("imagereff", fileRef.toString())

        downloadInMemory(fileRef)
    }

    private fun downloadInMemory(fileRef: StorageReference?) {
        if (fileRef != null) {

            val ONE_MEGABYTE = (1024 * 1024).toLong()
            fileRef.getBytes(ONE_MEGABYTE)
                    .addOnSuccessListener { bytes ->
                        val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        snapImageView?.setImageBitmap(bmp)
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
                    }
        } else {
            Toast.makeText(this, "Upload file before downloading", Toast.LENGTH_LONG).show()
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()

        FirebaseDatabase.getInstance().getReference().child("Users").child(auth.currentUser!!.uid).child("snaps").child(intent.getStringExtra("snapKey")).removeValue()

        FirebaseStorage.getInstance().getReference().child("images").child(intent.getStringExtra("imageName")).delete()
    }

}
