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
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.mybatis.jpetstore.core.event.*;

/**
 * The Class Item.
 *
 * @author Eduardo Macarron
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Item implements Serializable {

  private static final long serialVersionUID = -2159121673445254631L;

  private String itemId;
  private String productId;
  @JsonProperty("listPrice")
  private BigDecimal listPrice;
  @JsonProperty("unitCost")
  private BigDecimal unitCost;
  private int supplierId;
  private String status;
  private String attribute1;
  private String attribute2;
  private String attribute3;
  private String attribute4;
  private String attribute5;
  private Product product;
  private int quantity;
  private String itemStreamId;

  private List<DomainEvent> eventCache = new ArrayList<>();

  public Item(String itemId) {
    this.eventCache = new ArrayList<>();
    this.itemId = itemId;
  }

  public Item() {

  }

  public Item(String itemId, String productId, BigDecimal listPrice, BigDecimal unitCost, int supplierId, String status,
      String attribute1) {
    eventCache = new ArrayList<>();
    itemStreamId = UUID.randomUUID().toString();
    ItemCreatedEvent event = new ItemCreatedEvent(getStreamId(), Category.class.getName(), new Date().getTime(), itemId,
        productId, listPrice, unitCost, supplierId, status, attribute1);
    cause(event);
  }

  public void changeQuantity(int quantityChange) {
    InventoryUpdatedEvent event = new InventoryUpdatedEvent(getStreamId(), Item.class.getName(), this.itemId,
        quantityChange, new Date().getTime());
    cause(event);
  }

  public String getStreamId() {
    return Item.class.getName() + "." + this.itemStreamId;
  }

  private void cause(DomainEvent event) {
    mutate(event);
    eventCache.add(event);
  }

  public void mutate(DomainEvent event) {
    if (event instanceof InventoryUpdatedEvent) {
      applyInventoryChangeEvent((InventoryUpdatedEvent) event);
    } else if (event instanceof ItemCreatedEvent) {
      applyCreatedEvent((ItemCreatedEvent) event);
    } else {
      throw new IllegalArgumentException("Unsupported event type");
    }
  }

  private void applyCreatedEvent(ItemCreatedEvent event) {
    this.itemId = event.getItemId();
    this.productId = event.getProductId();
    this.listPrice = event.getListPrice();
    this.unitCost = event.getUnitCost();
    this.supplierId = event.getSupplierId();
    this.status = event.getStatus();
    this.attribute1 = event.getAttribute1();
  }

  private void applyInventoryChangeEvent(InventoryUpdatedEvent event) {
    this.quantity += event.getQuantityChange();
  }

  public String getItemId() {
    return itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId.trim();
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public int getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(int supplierId) {
    this.supplierId = supplierId;
  }

  public BigDecimal getListPrice() {
    return listPrice;
  }

  public void setListPrice(BigDecimal listPrice) {
    this.listPrice = listPrice;
  }

  public BigDecimal getUnitCost() {
    return unitCost;
  }

  public void setUnitCost(BigDecimal unitCost) {
    this.unitCost = unitCost;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getAttribute1() {
    return attribute1;
  }

  public void setAttribute1(String attribute1) {
    this.attribute1 = attribute1;
  }

  public String getAttribute2() {
    return attribute2;
  }

  public void setAttribute2(String attribute2) {
    this.attribute2 = attribute2;
  }

  public String getAttribute3() {
    return attribute3;
  }

  public void setAttribute3(String attribute3) {
    this.attribute3 = attribute3;
  }

  public String getAttribute4() {
    return attribute4;
  }

  public void setAttribute4(String attribute4) {
    this.attribute4 = attribute4;
  }

  public String getAttribute5() {
    return attribute5;
  }

  public void setAttribute5(String attribute5) {
    this.attribute5 = attribute5;
  }

  @Override
  public String toString() {
    return "(" + getItemId() + "-" + getProductId() + ")";
  }

  public List<DomainEvent> getEvents() {
    return this.eventCache;
  }
}
