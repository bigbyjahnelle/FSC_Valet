package server.model;

import java.util.Date;

/**
 * Firestore document: users/{uid}
 * roles: CUSTOMER, STAFF, ADMIN
 */
public class User {
    private String uid;
    private String email;
    private String fullName;
    private String phone;       // optional
    private String role;        // CUSTOMER, STAFF, ADMIN
    private Date createdAt;

    public User() {}

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
