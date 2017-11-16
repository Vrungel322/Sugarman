package com.sugarman.myb.models.googleLoginModel;
/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.Serializable;

//по сути SocialUser не нужен , но пример работает с ним - следовательно оставил, если че потом уберем


public class SocialUser implements Serializable {

  public String userId;
  public String accessToken;
  public String photoUrl;
  public Profile profile;

  public SocialUser() {
  }

  public SocialUser(SocialUser other) {
    this.userId = other.userId;
    this.accessToken = other.accessToken;
    this.photoUrl = other.photoUrl;
    if (other.profile != null) {
      this.profile = new Profile(other.profile);
    }
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SocialUser that = (SocialUser) o;

    return userId != null ? userId.equals(that.userId) : that.userId == null;
  }

  @Override public int hashCode() {
    return userId != null ? userId.hashCode() : 0;
  }

  @Override public String toString() {
    final StringBuilder sb = new StringBuilder("SocialUser{");
    sb.append("userId='").append(userId).append('\'');
    sb.append(", accessToken='").append(accessToken).append('\'');
    sb.append(", photoUrl='").append(photoUrl).append('\'');
    sb.append(", profile=").append(profile);
    sb.append('}');
    return sb.toString();
  }

  public static class Profile implements Serializable {
    public String name;
    public String firstName;
    public String lastName;
    public String email;
    public String pageLink;

    public Profile() {
    }

    public Profile(Profile other) {
      this.name = other.name;
      this.firstName = other.firstName;
      this.lastName = other.lastName;
      this.email = other.email;
      this.pageLink = other.pageLink;
    }

    @Override public String toString() {
      final StringBuilder sb = new StringBuilder("Profile{");
      sb.append("name='").append(name).append('\'');
      sb.append(", firstName='").append(firstName).append('\'');
      sb.append(", lastName='").append(name).append('\'');
      sb.append(", email='").append(email).append('\'');
      sb.append(", pageLink='").append(pageLink).append('\'');
      sb.append('}');
      return sb.toString();
    }
  }
}