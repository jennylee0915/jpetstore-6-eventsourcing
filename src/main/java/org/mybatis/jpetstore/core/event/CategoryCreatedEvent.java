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

public class CategoryCreatedEvent extends DomainEvent implements Serializable {

  private String categoryId;
  private String name;
  private String description;

  public CategoryCreatedEvent(@JsonProperty("streamId") String id, @JsonProperty("entityType") String entityType,
      @JsonProperty("timestamp") long timestamp, @JsonProperty("categoryId") String categoryId,
      @JsonProperty("name") String name, @JsonProperty("description") String description) {
    super(id, entityType, timestamp);
    this.categoryId = categoryId;
    this.name = name;
    this.description = description;
  }

  public String getCategoryId() {
    return categoryId;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
}
