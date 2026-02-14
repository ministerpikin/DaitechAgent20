package ique.daitechagent.model;

import java.io.Serializable;

public class User implements Serializable {
  private String agentRegion;
  private String avatarUrl;
  private String email;
  private boolean onlineStatus;
  private String onlineStatusValue;
  private String password;
  private String phone;
  private String userID;
  private String username;
  private int appversion;
  //private String supervisorRegion;

  public User() {
  }

  public User(String agentRegion, String avatarUrl, String email, boolean onlineStatus, String onlineStatusValue, String password, String phone, String userID, String username, int appversion) {
    this.agentRegion = agentRegion;
    this.avatarUrl = avatarUrl;
    this.email = email;
    this.onlineStatus = onlineStatus;
    this.onlineStatusValue = onlineStatusValue;
    this.password = password;
    this.phone = phone;
    this.userID = userID;
    this.username = username;
    this.appversion = appversion;
  }

  public String getAvatarUrl() {
    return this.avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl2) {
    this.avatarUrl = avatarUrl2;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email2) {
    this.email = email2;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password2) {
    this.password = password2;
  }

  public String getPhone() {
    return this.phone;
  }

  public void setPhone(String phone2) {
    this.phone = phone2;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username2) {
    this.username = username2;
  }

  public String getUserID() {
    return this.userID;
  }

  public void setUserID(String userID2) {
    this.userID = userID2;
  }

  public String getAgentRegion() {
    String str = "[Region not set]";
    if (this.agentRegion == null) {
      this.agentRegion = str;
    }
    if (this.agentRegion.isEmpty()) {
      this.agentRegion = str;
    }
    return this.agentRegion;
  }

  public void setAgentRegion(String agentRegion2) {
    this.agentRegion = agentRegion2;
  }

  public boolean isOnlineStatus() {
    return this.onlineStatus;
  }

  public void setOnlineStatus(boolean onlineStatus2) {
    this.onlineStatus = onlineStatus2;
  }

  public String getOnlineStatusValue() {
    return this.onlineStatusValue;
  }

  public void setOnlineStatusValue(String onlineStatusValue2) {
    this.onlineStatusValue = onlineStatusValue2;
  }

  public int getAppversion() {
    return appversion;
  }

  public void setAppversion(int appversion) {
    this.appversion = appversion;
  }
}
