package com.example.instagramclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.constraintlayout.solver.widgets.Snapshot
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class SnapActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance();
    var snapsListView: ListView? = null
    var emails: ArrayList<String> = ArrayList()
    var snaps: ArrayList<DataSnapshot> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snap)
        snapsListView = findViewById(R.id.snapsListView)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emails)
        snapsListView?.adapter = adapter

        FirebaseDatabase.getInstance().getReference().child("Users").child(auth.currentUser!!.uid).child("snaps").addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                emails.add(p0?.child("from").value as String)
                snaps.add(p0!!)
                adapter.notifyDataSetChanged()

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildRemoved(p0: DataSnapshot) {
                var i = 0
                for (snap: DataSnapshot in snaps) {
                    if (snap.key == p0?.key ){
                        snaps.removeAt(i)
                        emails.removeAt(i)
                    }
                    i++
                }
                adapter.notifyDataSetChanged()
            }
        })
        snapsListView?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, _ ->
            val snapshot = snaps.get(i)

            var intent = Intent(this, ViewSnapActivity::class.java)

            intent.putExtra("imageName",snapshot.child("imageName").value as String)
            intent.putExtra("message",snapshot.child("message").value as String)
            intent.putExtra("snapKey",snapshot.key)

            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val mainInflater = menuInflater
        mainInflater.inflate(R.menu.snaps,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if(item?.itemId == R.id.createSnap){
            val goToCreateSnapActivity = Intent(this, CreateSnapActivity::class.java)
            startActivity(goToCreateSnapActivity)
        }else if (item?.itemId == R.id.logOut){
            auth.signOut()
            Toast.makeText(this, "Logout successful", Toast.LENGTH_LONG ).show()
            finish()
        }

            return super.onOptionsItemSelected(item)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        auth.signOut()
    }
}
