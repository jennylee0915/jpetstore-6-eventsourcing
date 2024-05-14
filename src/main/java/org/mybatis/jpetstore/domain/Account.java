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
package org.mybatis.jpetstore.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.mybatis.jpetstore.core.event.*;

/**
 * The Class Account.
 *
 * @author Eduardo Macarron
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account implements Serializable {

  private static final long serialVersionUID = 8751282105532159742L;

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

  private List<DomainEvent> eventCache;

  public Account() {
    this.eventCache = new ArrayList<>();
    this.accountId = UUID.randomUUID().toString();
    // cause(new EntityCreatedEvent(getStreamId(), Account.class.getName(), new Date().getTime()));
  }

  public Account(String accountId) {
    this.eventCache = new ArrayList<>();
    this.accountId = accountId;
  }

  public Account(String username, String password, String repeatedPassword, String email, String firstName,
      String lastName, String status, String address1, String address2, String city, String state, String zip,
      String country, String phone, String favouriteCategoryId, String languagePreference, boolean listOption,
      boolean bannerOption, String bannerName) {
    eventCache = new ArrayList<>();
    accountId = UUID.randomUUID().toString();
    AccountCreatedEvent event = new AccountCreatedEvent(getStreamId(), Account.class.getName(), new Date().getTime(),
        accountId, username, password, repeatedPassword, email, firstName, lastName, status, address1, address2, city,
        state, zip, country, phone, favouriteCategoryId, languagePreference, listOption, bannerOption, bannerName);
    cause(event);
  }

  public String getAccountId() {
    return this.accountId;
  }

  public String getStreamId() {
    return Account.class.getName() + "." + this.accountId;
  }

  private void cause(DomainEvent event) {
    mutate(event);
    eventCache.add(event);
  }

  public void mutate(DomainEvent event) {
    if (event instanceof AccountCreatedEvent) {
      applyCreatedEvent((AccountCreatedEvent) event);
    } else if (event instanceof AttributeUpdatedEvent) {
      applyUpdatedEvent((AttributeUpdatedEvent) event);
    } else
      throw new IllegalArgumentException();
  }

  private void applyCreatedEvent(AccountCreatedEvent event) {
    this.username = event.getUsername();
    this.password = (String) event.getPassword();
    this.email = (String) event.getEmail();
    this.firstName = (String) event.getFirstName();
    this.lastName = (String) event.getLastName();
    this.status = (String) event.getStatus();
    this.address1 = (String) event.getAddress1();
    this.address2 = (String) event.getAddress2();
    this.city = (String) event.getCity();
    this.state = (String) event.getState();
    this.zip = (String) event.getZip();
    this.country = (String) event.getCountry();
    this.phone = (String) event.getPhone();
    this.favouriteCategoryId = (String) event.getFavouriteCategoryId();
    this.languagePreference = (String) event.getLanguagePreference();
    this.listOption = (boolean) event.isListOption();
    this.bannerOption = (boolean) event.isBannerOption();
    this.bannerName = (String) event.getBannerName();
  }

  private void applyUpdatedEvent(AttributeUpdatedEvent event) {
    switch (event.getName()) {
      case "username":
        this.username = (String) event.getValue();
        break;
      case "password":
        this.password = (String) event.getValue();
        break;
      case "email":
        this.email = (String) event.getValue();
        break;
      case "firstName":
        this.firstName = (String) event.getValue();
        break;
      case "lastName":
        this.lastName = (String) event.getValue();
        break;
      case "status":
        this.status = (String) event.getValue();
        break;
      case "address1":
        this.address1 = (String) event.getValue();
        break;
      case "address2":
        this.address2 = (String) event.getValue();
        break;
      case "city":
        this.city = (String) event.getValue();
        break;
      case "state":
        this.state = (String) event.getValue();
        break;
      case "zip":
        this.zip = (String) event.getValue();
        break;
      case "country":
        this.country = (String) event.getValue();
        break;
      case "phone":
        this.phone = (String) event.getValue();
        break;
      case "favouriteCategoryId":
        this.favouriteCategoryId = (String) event.getValue();
        break;
      case "languagePreference":
        this.languagePreference = (String) event.getValue();
        break;
      case "listOption":
        this.listOption = (boolean) event.getValue();
        break;
      case "bannerOption":
        this.bannerOption = (boolean) event.getValue();
        break;
      case "bannerName":
        this.bannerName = (String) event.getValue();
        break;
    }
  }

  private AttributeUpdatedEvent generateAttributeUpdatedEvent(String key, Object value) {
    AttributeUpdatedEvent event = new AttributeUpdatedEvent(getStreamId(), Account.class.getName(),
        new Date().getTime());
    event.setName(key);
    event.setValue(value);
    return event;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRepeatedPassword() {
    return repeatedPassword;
  }

  public void setRepeatedPassword(String repeatedPassword) {
    this.repeatedPassword = repeatedPassword;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getFirstName() {
    return firstName;
  }

  // @Validate(required = true, on = { "newAccount", "editAccount" })
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  // @Validate(required = true, on = { "newAccount", "editAccount" })
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getAddress1() {
    return address1;
  }

  public void setAddress1(String address1) {
    this.address1 = address1;
  }

  public String getAddress2() {
    return address2;
  }

  public void setAddress2(String address2) {
    this.address2 = address2;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getFavouriteCategoryId() {
    return favouriteCategoryId;
  }

  public void setFavouriteCategoryId(String favouriteCategoryId) {
    this.favouriteCategoryId = favouriteCategoryId;
  }

  public String getLanguagePreference() {
    return languagePreference;
  }

  public void setLanguagePreference(String languagePreference) {
    this.languagePreference = languagePreference;
  }

  public boolean isListOption() {
    return listOption;
  }

  public void setListOption(boolean listOption) {
    this.listOption = listOption;
  }

  public boolean isBannerOption() {
    return bannerOption;
  }

  public void setBannerOption(boolean bannerOption) {
    this.bannerOption = bannerOption;
  }

  public String getBannerName() {
    return bannerName;
  }

  public void setBannerName(String bannerName) {
    this.bannerName = bannerName;
  }

  public void reset() {
    eventCache.clear();
  }

  public List<DomainEvent> getEvents() {
    return this.eventCache;
  }

  @Override
  public String toString() {
    return String.format("Account{ accountId = '%s', username = '%s', email = '%s', firstName'%s', lastName = '%s', "
        + "status = '%s', adddress1 = '%s', address2 = '%s', city = '%s', state = '%s', zip = '%s', country = '%s', phone = '%s',"
        + "favouriteCategoryId = '%s', languagePreference = '%s', listOption = '%s', bannerOption = '%s', bannerName = '%s'}",
        accountId, username, password, email, firstName, lastName, status, address1, address2, city, state, zip,
        country, phone, favouriteCategoryId, languagePreference, listOption, bannerOption, bannerName);
  }

}
