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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "eventType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = ItemCreatedEvent.class, name = "org.mybatis.jpetstore.core.event.ItemCreatedEvent"),
    @JsonSubTypes.Type(value = InventoryUpdatedEvent.class, name = "org.mybatis.jpetstore.core.event.InventoryUpdatedEvent"),
    @JsonSubTypes.Type(value = OrderInsertedEvent.class, name = "org.mybatis.jpetstore.core.event.OrderInsertedEvent"),
    @JsonSubTypes.Type(value = LineItemAddedToOrderEvent.class, name = "org.mybatis.jpetstore.core.event.LineItemAddedToOrderEvent"),
    @JsonSubTypes.Type(value = OrderInitializedEvent.class, name = "org.mybatis.jpetstore.core.event.OrderInitializedEvent"),
    @JsonSubTypes.Type(value = OrderCreatedEvent.class, name = "org.mybatis.jpetstore.core.event.OrderCreatedEvent"),
    @JsonSubTypes.Type(value = AttributeUpdatedEvent.class, name = "org.mybatis.jpetstore.core.event.AttributeUpdatedEvent"),
    @JsonSubTypes.Type(value = EntityCreatedEvent.class, name = "org.mybatis.jpetstore.core.event.EntityCreatedEvent") })
public abstract class DomainEvent implements Serializable {
  private String entityType;
  private String eventType;
  private long timestamp;
  private String streamId;

  public DomainEvent(String streamId, String entityType, long timestamp) {
    this.entityType = entityType;
    this.timestamp = timestamp;
    this.streamId = streamId;
    this.eventType = this.getClass().getName();
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public String getStreamId() {
    return streamId;
  }

  public String getEntityType() {
    return entityType;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public java.lang.String toString() {
    return "DomainEvent{" + "entity=" + entityType + ", timestamp=" + timestamp + '}';
  }
}
