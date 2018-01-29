package com.sugarman.myb.models.chat_refactor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class Message {

  @SerializedName("_id") @Expose private String id;
  @SerializedName("user") @Expose private User user;
  @SerializedName("userID") @Expose private String userID;
  @SerializedName("roomID") @Expose private String roomID;
  @SerializedName("message") @Expose private String message;
  @SerializedName("localID") @Expose private String localID;
  @SerializedName("type") @Expose private Integer type;
  //@SerializedName("created") @Expose private Integer created;
  //@SerializedName("__v") @Expose private Integer v;
  @Getter @Setter @SerializedName("seenBy") @Expose private List<SeenBy> seenBy = null;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getUserID() {
    return userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  public String getRoomID() {
    return roomID;
  }

  public void setRoomID(String roomID) {
    this.roomID = roomID;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getLocalID() {
    return localID;
  }

  public void setLocalID(String localID) {
    this.localID = localID;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }
}
