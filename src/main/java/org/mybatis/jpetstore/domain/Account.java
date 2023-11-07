/*
 *    Copyright 2010-2023 the original author or authors.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.mybatis.jpetstore.core.event.AttributeUpdatedEvent;
import org.mybatis.jpetstore.core.event.DomainEvent;
import org.mybatis.jpetstore.core.event.EntityCreatedEvent;

/**
 * The Class Account.
 *
 * @author Eduardo Macarron
 */
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
    cause(new EntityCreatedEvent(getStreamId(), Account.class.getName(), new Date().getTime()));
  }

  public Account(String accountId) {
    this.eventCache = new ArrayList<>();
    this.accountId = accountId;
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
    if (event instanceof EntityCreatedEvent) {
      // pass
    } else if (event instanceof AttributeUpdatedEvent) {
      applyUpdatedEvent((AttributeUpdatedEvent) event);
    } else
      throw new IllegalArgumentException();
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
    if (this.username == null || !this.username.equals(username)) {
      AttributeUpdatedEvent event = generateAttributeUpdatedEvent("username", username);
      cause(event);
    }
    // this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    if (this.password == null || !this.password.equals(password)) {
      AttributeUpdatedEvent event = generateAttributeUpdatedEvent("password", password);
      cause(event);
    }
    // this.password = password;
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
    if (this.email == null || !this.email.equals(email)) {
      AttributeUpdatedEvent event = generateAttributeUpdatedEvent("email", email);
      cause(event);
    }
    // this.email = email;
  }

  public String getFirstName() {
    return firstName;
  }

  // @Validate(required = true, on = { "newAccount", "editAccount" })
  public void setFirstName(String firstName) {
    if (this.firstName == null || !this.firstName.equals(firstName)) {
      AttributeUpdatedEvent event = generateAttributeUpdatedEvent("firstName", firstName);
      cause(event);
    }
    // this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  // @Validate(required = true, on = { "newAccount", "editAccount" })
  public void setLastName(String lastName) {
    if (this.lastName == null || !this.lastName.equals(lastName)) {
      AttributeUpdatedEvent event = generateAttributeUpdatedEvent("lastName", lastName);
      cause(event);
    }
    // this.lastName = lastName;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    if (this.status == null || !this.status.equals(status)) {
      AttributeUpdatedEvent event = generateAttributeUpdatedEvent("status", status);
      cause(event);
    }
    // this.status = status;
  }

  public String getAddress1() {
    return address1;
  }

  public void setAddress1(String address1) {
    if (this.address1 == null || !this.address1.equals(address1)) {
      AttributeUpdatedEvent event = generateAttributeUpdatedEvent("address1", address1);
      cause(event);
    }
    // this.address1 = address1;
  }

  public String getAddress2() {
    return address2;
  }

  public void setAddress2(String address2) {
    if (this.address2 == null || !this.address2.equals(address2)) {
      AttributeUpdatedEvent event = generateAttributeUpdatedEvent("address2", address2);
      cause(event);
    }
    // this.address2 = address2;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    if (this.city == null || !this.city.equals(city)) {
      AttributeUpdatedEvent event = generateAttributeUpdatedEvent("city", city);
      cause(event);
    }
    // this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    if (this.state == null || !this.state.equals(state)) {
      AttributeUpdatedEvent event = generateAttributeUpdatedEvent("state", state);
      cause(event);
    }
    // this.state = state;
  }

  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    if (this.zip == null || !this.zip.equals(zip)) {
      AttributeUpdatedEvent event = generateAttributeUpdatedEvent("zip", zip);
      cause(event);
    }
    // this.zip = zip;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    if (this.country == null || !this.country.equals(country)) {
      AttributeUpdatedEvent event = generateAttributeUpdatedEvent("country", country);
      cause(event);
    }
    // this.country = country;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    if (this.phone == null || !this.phone.equals(phone)) {
      AttributeUpdatedEvent event = generateAttributeUpdatedEvent("phone", phone);
      cause(event);
    }
    // this.phone = phone;
  }

  public String getFavouriteCategoryId() {
    return favouriteCategoryId;
  }

  public void setFavouriteCategoryId(String favouriteCategoryId) {
    if (this.favouriteCategoryId == null || !this.favouriteCategoryId.equals(favouriteCategoryId)) {
      AttributeUpdatedEvent event = generateAttributeUpdatedEvent("favouriteCategoryId", favouriteCategoryId);
      cause(event);
    }
    // this.favouriteCategoryId = favouriteCategoryId;
  }

  public String getLanguagePreference() {
    return languagePreference;
  }

  public void setLanguagePreference(String languagePreference) {
    if (this.languagePreference == null || !this.languagePreference.equals(languagePreference)) {
      AttributeUpdatedEvent event = generateAttributeUpdatedEvent("languagePreference", languagePreference);
      cause(event);
    }
    // this.languagePreference = languagePreference;
  }

  public boolean isListOption() {
    return listOption;
  }

  public void setListOption(boolean listOption) {
    if (this.listOption != listOption) {
      AttributeUpdatedEvent event = generateAttributeUpdatedEvent("listOption", listOption);
      cause(event);
    }
    // this.listOption = listOption;
  }

  public boolean isBannerOption() {
    return bannerOption;
  }

  public void setBannerOption(boolean bannerOption) {
    if (this.bannerOption != bannerOption) {
      AttributeUpdatedEvent event = generateAttributeUpdatedEvent("bannerOption", bannerOption);
      cause(event);
    }
    // this.bannerOption = bannerOption;
  }

  public String getBannerName() {
    return bannerName;
  }

  public void setBannerName(String bannerName) {
    if (this.bannerName != bannerName) {
      AttributeUpdatedEvent event = generateAttributeUpdatedEvent("bannerName", bannerName);
      cause(event);
    }
    // this.bannerName = bannerName;
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
