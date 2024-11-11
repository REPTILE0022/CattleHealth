package com.example.cattlehealth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class ChatForumActivity extends AppCompatActivity {

    private EditText editTextMessage;
    private Button buttonSend;
    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    //Design an application for the church that has the ability to store church members information
    // including their status information, and the church leaders have to have the ability to view all
    // the information about the member.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_forum);

        // Initialize views
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);

        // Initialize Firebase Auth and Database Reference
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("messages");

        // Initialize message list and adapter
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);

        // Set up RecyclerView
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);

        // Set up send button click listener
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        // Load messages from Firebase
        loadMessages();
    }

    private void sendMessage() {
        // Get the message text from the EditText
        String messageText = editTextMessage.getText().toString().trim();

        // Check if the message text is empty
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current user's ID and email (or username)
        String userId = mAuth.getCurrentUser().getUid();
        String userName = mAuth.getCurrentUser().getEmail(); // Replace this with a user name field if available

        // Create a new message object
        Message message = new Message(userId, userName, messageText);

        // Push the message to Firebase
        mDatabase.push().setValue(message);

        // Clear the EditText
        editTextMessage.setText("");
    }

    private void loadMessages() {
        // Add a value event listener to the messages reference in Firebase
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the current message list
                messageList.clear();

                // Iterate through all messages in the data snapshot
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get the message object from the snapshot
                    Message message = snapshot.getValue(Message.class);

                    // Add the message to the message list
                    messageList.add(message);
                }

                // Notify the adapter that the data set has changed
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Show an error message if data loading is cancelled
                Toast.makeText(ChatForumActivity.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
