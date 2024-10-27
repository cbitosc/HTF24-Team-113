package com.hacktoberfest.patientmonitor.Models;

public class MessageModel {
    String senderUid;
    long messageAt;
    String receiverUid;
    String message;
    String image;
    String postId;
    String messageId;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }


    public MessageModel() {

    }

    public MessageModel(String senderUid, long messageAt, String receiverUid) {
        this.senderUid = senderUid;
        this.messageAt = messageAt;
        this.receiverUid = receiverUid;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public long getMessageAt() {
        return messageAt;
    }

    public void setMessageAt(long messageAt) {
        this.messageAt = messageAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }
}

