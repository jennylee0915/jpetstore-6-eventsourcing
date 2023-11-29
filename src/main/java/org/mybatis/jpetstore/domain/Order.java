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
import java.math.BigDecimal;
import java.util.*;

import org.mybatis.jpetstore.core.event.*;

public class Order implements Serializable {

  private static final long serialVersionUID = 6321792448424424931L;

  private String orderId;
  private String username;
  private Date orderDate;
  private String shipAddress1;
  private String shipAddress2;
  private String shipCity;
  private String shipState;
  private String shipZip;
  private String shipCountry;
  private String billAddress1;
  private String billAddress2;
  private String billCity;
  private String billState;
  private String billZip;
  private String billCountry;
  private String courier;
  private BigDecimal totalPrice;
  private String billToFirstName;
  private String billToLastName;
  private String shipToFirstName;
  private String shipToLastName;
  private String creditCard;
  private String expiryDate;
  private String cardType;
  private String locale;
  private String status;
  private List<LineItem> lineItems = new ArrayList<>();
  private List<DomainEvent> eventCache = new ArrayList<>();

  public Order(String orderId) {
    this.eventCache = new ArrayList<>();
    this.orderId = orderId;
  }

  public Order() {
    this.eventCache = new ArrayList<>();
    this.orderId = UUID.randomUUID().toString();
    OrderCreatedEvent event = new OrderCreatedEvent(getStreamId(), Order.class.getName(), orderId,
        new Date().getTime());
    cause(event);
  }

  public String getOrderId() {
    return this.orderId;
  }

  public String getStreamId() {
    return Order.class.getName() + "." + this.orderId;
  }

  // 產生事件並加入到eventcache，同時負責調用mutate確保聚合根“內部”狀態更新
  public void cause(DomainEvent event) {
    mutate(event);
    eventCache.add(event);
  }

  // 基於特定事件改變聚合根“內部”狀態
  public void mutate(DomainEvent event) {
    if (event instanceof OrderCreatedEvent) {
      // pass
    } else if (event instanceof OrderInitializedEvent) {
      OrderInitializedEvent initEvent = (OrderInitializedEvent) event;
      Account account = initEvent.getAccount();
      Cart cart = initEvent.getCart();

      username = account.getUsername();
      orderDate = new Date();

      shipToFirstName = account.getFirstName();
      shipToLastName = account.getLastName();
      shipAddress1 = account.getAddress1();
      shipAddress2 = account.getAddress2();
      shipCity = account.getCity();
      shipState = account.getState();
      shipZip = account.getZip();
      shipCountry = account.getCountry();

      billToFirstName = account.getFirstName();
      billToLastName = account.getLastName();
      billAddress1 = account.getAddress1();
      billAddress2 = account.getAddress2();
      billCity = account.getCity();
      billState = account.getState();
      billZip = account.getZip();
      billCountry = account.getCountry();

      totalPrice = cart.getSubTotal();

      creditCard = "999 9999 9999 9999";
      expiryDate = "12/03";
      cardType = "Visa";
      courier = "UPS";
      locale = "CA";
      status = "P";

    } else if (event instanceof LineItemAddedToOrderEvent) {
      LineItemAddedToOrderEvent lineItemEvent = (LineItemAddedToOrderEvent) event;
      LineItem lineItem = new LineItem();
      lineItem.setOrderId(orderId);
      lineItem.setLineNumber(lineItems.size() + 1);
      lineItem.setItem(lineItemEvent.getItem());
      lineItem.setItemId(lineItemEvent.getItemId());
      lineItem.setQuantity(lineItemEvent.getQuantity());
      lineItem.setUnitPrice(lineItemEvent.getUnitPrice());
      lineItem.setTotal(lineItem.getUnitPrice().multiply(new BigDecimal(lineItem.getQuantity())));
      // 加入orde的lineitenm表中
      addLineItem(lineItem);
    } else if (event instanceof OrderInsertedEvent) {
      // pass
      // this.status = "Inserted";
    } else if (event instanceof InventoryUpdatedEvent) {
      // pass
    } else {
      throw new IllegalArgumentException();
    }
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Date getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(Date orderDate) {
    this.orderDate = orderDate;
  }

  public String getShipAddress1() {
    return shipAddress1;
  }

  public void setShipAddress1(String shipAddress1) {
    this.shipAddress1 = shipAddress1;
  }

  public String getShipAddress2() {
    return shipAddress2;
  }

  public void setShipAddress2(String shipAddress2) {
    this.shipAddress2 = shipAddress2;
  }

  public String getShipCity() {
    return shipCity;
  }

  public void setShipCity(String shipCity) {
    this.shipCity = shipCity;
  }

  public String getShipState() {
    return shipState;
  }

  public void setShipState(String shipState) {
    this.shipState = shipState;
  }

  public String getShipZip() {
    return shipZip;
  }

  public void setShipZip(String shipZip) {
    this.shipZip = shipZip;
  }

  public String getShipCountry() {
    return shipCountry;
  }

  public void setShipCountry(String shipCountry) {
    this.shipCountry = shipCountry;
  }

  public String getBillAddress1() {
    return billAddress1;
  }

  public void setBillAddress1(String billAddress1) {
    this.billAddress1 = billAddress1;
  }

  public String getBillAddress2() {
    return billAddress2;
  }

  public void setBillAddress2(String billAddress2) {
    this.billAddress2 = billAddress2;
  }

  public String getBillCity() {
    return billCity;
  }

  public void setBillCity(String billCity) {
    this.billCity = billCity;
  }

  public String getBillState() {
    return billState;
  }

  public void setBillState(String billState) {
    this.billState = billState;
  }

  public String getBillZip() {
    return billZip;
  }

  public void setBillZip(String billZip) {
    this.billZip = billZip;
  }

  public String getBillCountry() {
    return billCountry;
  }

  public void setBillCountry(String billCountry) {
    this.billCountry = billCountry;
  }

  public String getCourier() {
    return courier;
  }

  public void setCourier(String courier) {
    this.courier = courier;
  }

  public BigDecimal getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(BigDecimal totalPrice) {
    this.totalPrice = totalPrice;
  }

  public String getBillToFirstName() {
    return billToFirstName;
  }

  public void setBillToFirstName(String billToFirstName) {
    this.billToFirstName = billToFirstName;
  }

  public String getBillToLastName() {
    return billToLastName;
  }

  public void setBillToLastName(String billToLastName) {
    this.billToLastName = billToLastName;
  }

  public String getShipToFirstName() {
    return shipToFirstName;
  }

  public void setShipToFirstName(String shipFoFirstName) {
    this.shipToFirstName = shipFoFirstName;
  }

  public String getShipToLastName() {
    return shipToLastName;
  }

  public void setShipToLastName(String shipToLastName) {
    this.shipToLastName = shipToLastName;
  }

  public String getCreditCard() {
    return creditCard;
  }

  public void setCreditCard(String creditCard) {
    this.creditCard = creditCard;
  }

  public String getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(String expiryDate) {
    this.expiryDate = expiryDate;
  }

  public String getCardType() {
    return cardType;
  }

  public void setCardType(String cardType) {
    this.cardType = cardType;
  }

  public String getLocale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setLineItems(List<LineItem> lineItems) {
    this.lineItems = lineItems;
  }

  public List<LineItem> getLineItems() {
    return lineItems;
  }

  /**
   * Inits the order.
   *
   * @param account
   *          the account
   * @param cart
   *          the cart
   */
  public void initOrder(Account account, Cart cart) {

    cause(new OrderInitializedEvent(getStreamId(), Order.class.getName(), account, cart, new Date().getTime()));

    for (CartItem cartItem : cart.getCartItemList()) {
      LineItemAddedToOrderEvent event = new LineItemAddedToOrderEvent(this.getStreamId(), Order.class.getName(),
          cartItem.getItem(), cartItem.getItem().getItemId(), cartItem.getQuantity(), cartItem.getItem().getListPrice(),
          new Date().getTime());
      cause(event);
    }

    // Iterator<CartItem> i = cart.getAllCartItems();
    // while (i.hasNext()) {
    // CartItem cartItem = i.next();
    // addLineItem(cartItem);
    // }

  }

  public void addLineItem(CartItem cartItem) {
    LineItem lineItem = new LineItem(lineItems.size() + 1, cartItem);
    addLineItem(lineItem);
  }

  public void addLineItem(LineItem lineItem) {
    lineItems.add(lineItem);
  }

  public List<DomainEvent> getEvents() {
    return this.eventCache;
  }

  public OrderDTO toDTO() {
    OrderDTO dto = new OrderDTO();
    dto.setOrderId(this.orderId);
    dto.setUsername(this.username);
    dto.setOrderDate(this.orderDate);
    dto.setShipAddress1(this.shipAddress1);
    dto.setShipAddress2(this.shipAddress2);
    dto.setShipCity(this.shipCity);
    dto.setShipState(this.shipState);
    dto.setShipZip(this.shipZip);
    dto.setShipCountry(this.shipCountry);
    dto.setBillAddress1(this.billAddress1);
    dto.setBillAddress2(this.billAddress2);
    dto.setBillCity(this.billCity);
    dto.setBillState(this.billState);
    dto.setBillZip(this.billZip);
    dto.setBillCountry(this.billCountry);
    dto.setCourier(this.courier);
    dto.setTotalPrice(this.totalPrice);
    dto.setBillToFirstName(this.billToFirstName);
    dto.setBillToLastName(this.billToLastName);
    dto.setShipToFirstName(this.shipToFirstName);
    dto.setShipToLastName(this.shipToLastName);
    dto.setCreditCard(this.creditCard);
    dto.setExpiryDate(this.expiryDate);
    dto.setCardType(this.cardType);
    dto.setLocale(this.locale);
    dto.setStatus(this.status);
    dto.setLineItems(this.lineItems);

    return dto;
  }

  public void reset() {
    eventCache.clear();
  }

  @Override
  public String toString() {
    return String.format("Order{ orderId = '%s', username = '%s', orderDate = '%s' ", orderId, username, orderDate);
  }

}