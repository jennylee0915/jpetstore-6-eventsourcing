/*
 *    Copyright 2010-2024 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.jpetstore.core.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class AccountCreatedEvent extends DomainEvent implements Serializable {
  private String accountId;
  private String username;
  private String password;
  private String repeatedPassword;
  private String email;
  private String firstName;
  private String lastName;
  private String status;
  private String address1;
  private String address2;
  private String city;
  private String state;
  private String zip;
  private String country;
  private String phone;
  private String favouriteCategoryId;
  private String languagePreference;
  private boolean listOption;
  private boolean bannerOption;
  private String bannerName;

  public AccountCreatedEvent(@JsonProperty("streamId") String id, @JsonProperty("entityType") String entityType,
      @JsonProperty("timestamp") long timestamp, @JsonProperty("accountId") String accountId,
      @JsonProperty("username") String username, @JsonProperty("password") String password,
      @JsonProperty("repeatedPassword") String repeatedPassword, String email, String firstName, String lastName,
      String status, String address1, String address2, String city, String state, String zip, String country,
      String phone, String favouriteCategoryId, String languagePreference, boolean listOption, boolean bannerOption,
      String bannerName) {
    super(id, entityType, timestamp);
    this.accountId = accountId;
    this.username = username;
    this.password = password;
    this.repeatedPassword = repeatedPassword;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.status = status;
    this.address1 = address1;
    this.address2 = address2;
    this.city = city;
    this.state = state;
    this.zip = zip;
    this.country = country;
    this.phone = phone;
    this.favouriteCategoryId = favouriteCategoryId;
    this.languagePreference = languagePreference;
    this.listOption = listOption;
    this.bannerOption = bannerOption;
    this.bannerName = bannerName;
  }

  public String getAccountId() {
    return accountId;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getStatus() {
    return status;
  }

  @Override
  public long getTimestamp() {
    return super.getTimestamp();
  }

  @Override
  public String getEventType() {
    return super.getEventType();
  }

  @Override
  public String getEntityType() {
    return super.getEntityType();
  }

  @Override
  public String getStreamId() {
    return super.getStreamId();
  }

  public String getAddress1() {
    return address1;
  }

  public String getAddress2() {
    return address2;
  }

  public String getBannerName() {
    return bannerName;
  }

  public String getCity() {
    return city;
  }

  public String getCountry() {
    return country;
  }

  public String getEmail() {
    return email;
  }

  public String getFavouriteCategoryId() {
    return favouriteCategoryId;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getPhone() {
    return phone;
  }

  public String getLanguagePreference() {
    return languagePreference;
  }

  public String getRepeatedPassword() {
    return repeatedPassword;
  }

  public String getState() {
    return state;
  }

  public String getZip() {
    return zip;
  }

  public boolean isListOption() {
    return listOption;
  }

  public boolean isBannerOption() {
    return bannerOption;
  }
}
