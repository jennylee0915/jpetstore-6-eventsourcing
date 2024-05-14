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
import java.math.BigDecimal;

public class ItemCreatedEvent extends DomainEvent implements Serializable {
  private String productId;
  private String itemId;
  private BigDecimal listPrice;
  private BigDecimal unitCost;
  private int supplierId;
  private String status;
  private String attribute1;

  // itemid, productid, listprice, unitcost, supplier, status, attr1
  public ItemCreatedEvent(@JsonProperty("streamId") String id, @JsonProperty("entityType") String entityType,
      @JsonProperty("timestamp") long timestamp, @JsonProperty("itemId") String itemId,
      @JsonProperty("productId") String productId, @JsonProperty("listPrice") BigDecimal listPrice,
      @JsonProperty("unitCost") BigDecimal unitCost, @JsonProperty("supplierId") int supplierId,
      @JsonProperty("status") String status, @JsonProperty("attribute1") String attribute1) {
    super(id, entityType, timestamp);
    this.productId = productId;
    this.itemId = itemId;
    this.listPrice = listPrice;
    this.unitCost = unitCost;
    this.supplierId = supplierId;
    this.status = status;
    this.attribute1 = attribute1;

  }

  public String getProductId() {
    return productId;
  }

  public String getItemId() {
    return itemId;
  }

  public BigDecimal getListPrice() {
    return listPrice;
  }

  public BigDecimal getUnitCost() {
    return unitCost;
  }

  public int getSupplierId() {
    return supplierId;
  }

  public String getStatus() {
    return status;
  }

  public String getAttribute1() {
    return attribute1;
  }
}
