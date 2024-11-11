package com.example.cattlehealth;

public class Message {
    private String userId;
    private String userName;
    private String messageText;

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Message.class)
    }

    public Message(String userId, String userName, String messageText) {
        this.userId = userId;
        this.userName = userName;
        this.messageText = messageText;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessageText() {
        return messageText;
    }
}
