package com.example.instagramclone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*


class CreateSnapActivity : AppCompatActivity() {

    var createSnapimageView: ImageView? = null
    var editTextMessage: EditText? = null
    val imageName = UUID.randomUUID().toString() + ".jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)
        createSnapimageView =findViewById(R.id.createSnapimageView)
        editTextMessage = findViewById(R.id.editTextMessage)
    }

    fun getPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    fun ChooseImageClicked(view: View){
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            getPhoto()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val selectedImage = data!!.data
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                createSnapimageView?.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
   override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

       if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto()
            }
        }
    }

    fun nextButtonClicked(view: View){

        // Get the data from an ImageView as bytes
        val bitmap = (createSnapimageView?.getDrawable() as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()


        val uploadTask = FirebaseStorage.getInstance().getReference().child("images").child(imageName).putBytes(data)
        uploadTask.addOnFailureListener( {
            // Handle unsuccessful uploads
            Toast.makeText(this, "upload failed", Toast.LENGTH_SHORT ).show()

        }).addOnSuccessListener {
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.

            Toast.makeText(this, "upload succes", Toast.LENGTH_SHORT ).show()
           // it.storage.downloadUrl

           //var downloadURL = FirebaseStorage.getInstance().getReference().child("images").child(imageName).downloadUrl
            //val downloadURL= "https://firebasestorage.googleapis.com/v0/b/instagram-clone-8b7b6.appspot.com/o/images%2F$imageName?alt=media&token=515573bf-9e57-4e44-b46c-ac094426d263"



//          Log.i("downloadUrl", downloadURL.toString())
            val intent = Intent(this, ChooseUserActivity::class.java )
            intent.putExtra("imageName", imageName)
            intent.putExtra("message", editTextMessage?.text.toString())
             startActivity(intent)
        }
    }

}
