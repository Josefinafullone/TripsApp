package com.example.mytripsjourney;

import com.google.firebase.firestore.Exclude;

public class UserInfo {
    private String documentId;
    private String userEmail;
    private String userName;
    private String userPhone;

    public UserInfo(String userEmail, String userName, String userPhone) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.userPhone = userPhone;
    }
    @Exclude
    public String getDocumentId() {
        return documentId;
    }
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    public String getUserEmail() { return userEmail; }
    public String getUserName() {
        return userName;
    }
}
