package com.example.cattlehealth;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminChatForumActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMessages;
    private EditText editTextMessage;
    private Button buttonSend;
    private List<Message> messageList;
    private MessageAdapter adapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chat_forum);

        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("messages");

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(adapter);

        buttonSend.setOnClickListener(view -> {
            String messageText = editTextMessage.getText().toString().trim();

            if (TextUtils.isEmpty(messageText)) {
                Toast.makeText(AdminChatForumActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                return;
            }

            String senderId = mAuth.getCurrentUser().getUid();
            String senderName = "Admin"; // As this is the Admin's side

            Message message = new Message(senderId, senderName, messageText);
            mDatabase.push().setValue(message).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    editTextMessage.setText("");
                } else {
                    Toast.makeText(AdminChatForumActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                }
            });
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    messageList.add(message);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AdminChatForumActivity.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
