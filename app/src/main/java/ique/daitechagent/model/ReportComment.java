package ique.daitechagent.model;

public class ReportComment {
    private long createdAt;
    private String key;
    private String message;
    private User sender;
    private String userID;

    public ReportComment() {
    }

    public ReportComment(String message2, User sender2, long createdAt2, String key2, String userID2) {
        this.message = message2;
        this.sender = sender2;
        this.createdAt = createdAt2;
        this.key = key2;
        this.userID = userID2;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message2) {
        this.message = message2;
    }

    public User getSender() {
        return this.sender;
    }

    public void setSender(User sender2) {
        this.sender = sender2;
    }

    public long getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(long createdAt2) {
        this.createdAt = createdAt2;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key2) {
        this.key = key2;
    }

    public String getUserID() {
        return this.userID;
    }

    public void setUserID(String userID2) {
        this.userID = userID2;
    }
}
