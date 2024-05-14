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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.mybatis.jpetstore.core.event.AttributeUpdatedEvent;
import org.mybatis.jpetstore.core.event.CategoryCreatedEvent;
import org.mybatis.jpetstore.core.event.DomainEvent;
import org.mybatis.jpetstore.core.event.EntityCreatedEvent;

/**
 * The Class Category.
 *
 * @author Eduardo Macarron
 */
public class Category implements Serializable {

  private static final long serialVersionUID = 3992469837058393712L;

  private String categoryId;
  private String categoryStreamId;
  private String name;
  private String description;

  private List<DomainEvent> eventCache;

  public Category() {
    eventCache = new ArrayList<>();
    categoryId = UUID.randomUUID().toString();
    // cause(new EntityCreatedEvent(getStreamId(), Category.class.getName(), new Date().getTime()));
  }

  public Category(String categoryId) {
    eventCache = new ArrayList<>();
    this.categoryId = categoryId;
  }

  public Category(String categoryId, String name, String description) {
    eventCache = new ArrayList<>();
    categoryStreamId = UUID.randomUUID().toString();
    CategoryCreatedEvent event = new CategoryCreatedEvent(getStreamId(), Category.class.getName(), new Date().getTime(),
        categoryId, name, description);
    cause(event);
  }

  private AttributeUpdatedEvent generateAttributeUpdatedEvent(String key, Object value) {
    AttributeUpdatedEvent event = new AttributeUpdatedEvent(getStreamId(), Category.class.getName(),
        new Date().getTime());
    event.setName(key);
    event.setValue(value);
    return event;
  }

  private String getStreamId() {
    return Category.class.getName() + "." + categoryStreamId;
  }

  private void cause(DomainEvent event) {
    mutate(event);
    eventCache.add(event);
  }

  public void mutate(DomainEvent event) { // 依據進來的事件改變物件本身狀態
    if (event instanceof EntityCreatedEvent) {
      // applyCreatedEvent((EntityCreatedEvent<Category>) event);
    } else if (event instanceof CategoryCreatedEvent) {
      applyCreatedEvent((CategoryCreatedEvent) event);
    } else if (event instanceof AttributeUpdatedEvent) {
      applyUpdatedEvent((AttributeUpdatedEvent) event);
    } else
      throw new IllegalArgumentException();

  }

  private void applyCreatedEvent(CategoryCreatedEvent event) {
    this.categoryId = event.getCategoryId();
    this.name = event.getName();
    this.description = event.getDescription();
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

  public void reset() {
    eventCache.clear();
  }

  public List<DomainEvent> getEvents() {
    return eventCache;
  }

  public String getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(String categoryId) {
    this.categoryId = categoryId.trim();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return String.format("Category{categoryId='%s', name='%s', description='%s'}", categoryId, name, description);
  }

}
