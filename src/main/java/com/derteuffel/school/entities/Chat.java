package com.derteuffel.school.entities;

import lombok.Data;

@Data
public class Chat {

    private MessageType type;
    private String content;
    private String sender;

    public enum MessageType{
        JOIN,
        CHAT,
        LEAVE
    }
}
