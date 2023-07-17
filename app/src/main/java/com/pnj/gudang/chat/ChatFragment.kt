package com.pnj.gudang.chat

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pnj.gudang.R
import com.pnj.gudang.databinding.FragmentChatBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class ChatFragment : Fragment(R.layout.fragment_chat ){
    private var _binding : FragmentChatBinding? = null
    private lateinit var database : DatabaseReference

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatArrayList: ArrayList<Chat>
    private lateinit var chatAdapter: ChatAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentChatBinding.bind(view)
        _binding = binding

        firebaseAuth = FirebaseAuth.getInstance()

        chatRecyclerView = binding.chatListView
        chatRecyclerView.layoutManager = LinearLayoutManager(context)
        chatRecyclerView.setHasFixedSize(true)

        chatArrayList = arrayListOf()
        chatAdapter = ChatAdapter(chatArrayList)

        chatRecyclerView.adapter = chatAdapter

        load_chat_data()

        binding.BtnSendChat.setOnClickListener {
            val message = binding.TxtChatBox.text.toString()
            val username = firebaseAuth.currentUser?.email.toString()
            Log.e("chat", message)
            send_chat(username,message)
        }
    }

    private fun load_chat_data(){
        database = FirebaseDatabase.getInstance().getReference("chat_db")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    chatArrayList.clear()
                    for(chatSnapshoot in snapshot.children){
                        val chat_data = chatSnapshoot.getValue(Chat::class.java)
                        chatArrayList.add(chat_data!!)
                    }
                    chatRecyclerView.adapter = ChatAdapter(chatArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun send_chat(username: String, message:String){

        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val current_time = formatter.format(time).toString()

        val chat = Chat(username, message, current_time)

        database = FirebaseDatabase.getInstance().getReference("chat_db")

        database.child(current_time).setValue(chat).addOnSuccessListener {
            _binding?.TxtChatBox?.text?.clear()
        }.addOnFailureListener {
            Toast.makeText(context,"Kirim Chat gagal", Toast.LENGTH_SHORT).show()
            Log.e("gagal","kirim gagal")
        }
    }
}
