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
 * The Class Product.
 *
 * @author Eduardo Macarron
 */
public class Product implements Serializable {

  private static final long serialVersionUID = -7492639752670189553L;

  private String productId;
  private String categoryId;
  private String name;
  private String description;
  private List<DomainEvent> eventCache;

  public Product() {
    eventCache = new ArrayList<>();
    productId = UUID.randomUUID().toString();
    cause(new EntityCreatedEvent(getStreamId(), Category.class.getName(), new Date().getTime()));
  }

  public Product(String productId) {
    eventCache = new ArrayList<>();
    this.productId = productId;
  }

  private AttributeUpdatedEvent generateAttributeUpdatedEvent(String key, Object value) {
    AttributeUpdatedEvent event = new AttributeUpdatedEvent(getStreamId(), Product.class.getName(),
        new Date().getTime());
    event.setName(key);
    event.setValue(value);
    return event;
  }

  private String getStreamId() {
    return Product.class.getName() + "." + productId;
  }

  private void cause(DomainEvent event) {
    mutate(event);
    eventCache.add(event);
  }

  public void mutate(DomainEvent event) { // 依據進來的事件改變物件本身狀態
    if (event instanceof EntityCreatedEvent) {
      // applyCreatedEvent((EntityCreatedEvent<Category>) event);
    } else if (event instanceof AttributeUpdatedEvent) {
      applyUpdatedEvent((AttributeUpdatedEvent) event);
    } else
      throw new IllegalArgumentException();

  }

  private void applyUpdatedEvent(AttributeUpdatedEvent event) {
    switch (event.getName()) {
      case "name":
        this.name = (String) event.getValue();
        break;
      case "description":
        this.description = (String) event.getValue();
        break;
    }
  }

  public List<DomainEvent> getEvents() {
    return eventCache;
  }

  public void reset() {
    eventCache.clear();
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId.trim();
  }

  public String getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(String categoryId) {
    this.categoryId = categoryId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    AttributeUpdatedEvent event = generateAttributeUpdatedEvent("name", name);
    cause(event);
    // this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    AttributeUpdatedEvent event = generateAttributeUpdatedEvent("description", description);
    cause(event);
    // this.description = description;
  }

  @Override
  public String toString() {
    return getName();
  }

}
