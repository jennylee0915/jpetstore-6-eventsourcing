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
package org.mybatis.jpetstore.core.event;

import org.mybatis.jpetstore.domain.Account;
import org.mybatis.jpetstore.domain.Cart;

public class OrderInitializedEvent extends DomainEvent {

  private Account account;
  private Cart cart;

  public OrderInitializedEvent(String streamId, String entityType, Account account, Cart cart, long timestamp) {
    super(streamId, entityType, timestamp);
    this.account = account;
    this.cart = cart;
  }

  public Account getAccount() {
    return account;
  }

  public Cart getCart() {
    return cart;
  }
}