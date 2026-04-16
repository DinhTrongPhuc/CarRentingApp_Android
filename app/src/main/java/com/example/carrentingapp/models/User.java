package com.example.carrentingapp.models;

public class User {
    private String uid, fullName, email, phone, avatarUrl, role;
    private long createdAt;

    public User() {}

    public User(String uid, String fullName, String email, String phone, String role) {
        this.uid = uid; this.fullName = fullName; this.email = email;
        this.phone = phone; this.role = role;
        this.createdAt = System.currentTimeMillis();
    }

    public String getUid() { return uid; } public void setUid(String v) { uid = v; }
    public String getFullName() { return fullName; } public void setFullName(String v) { fullName = v; }
    public String getEmail() { return email; } public void setEmail(String v) { email = v; }
    public String getPhone() { return phone; } public void setPhone(String v) { phone = v; }
    public String getAvatarUrl() { return avatarUrl; } public void setAvatarUrl(String v) { avatarUrl = v; }
    public String getRole() { return role; } public void setRole(String v) { role = v; }
    public long getCreatedAt() { return createdAt; } public void setCreatedAt(long v) { createdAt = v; }
}
