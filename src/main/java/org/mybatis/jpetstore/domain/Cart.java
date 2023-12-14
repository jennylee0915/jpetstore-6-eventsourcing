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

/**
 * The Class Cart.
 *
 * @author Eduardo Macarron
 */
public class Cart implements Serializable {

  private static final long serialVersionUID = 8329559983943337176L;

  private final Map<String, CartItem> itemMap = Collections.synchronizedMap(new HashMap<>());
  private final List<CartItem> itemList = new ArrayList<>();
  private List<DomainEvent> eventCache = new ArrayList<>();
  private String cartId;

  public Cart() {
    this.eventCache = new ArrayList<>();
    this.cartId = UUID.randomUUID().toString();
  }
  // public Cart(String cartId) {
  // this.eventCache = new ArrayList<>();
  // this.cartId = cartId;
  // }

  public Iterator<CartItem> getCartItems() {
    return itemList.iterator();
  }

  public List<CartItem> getCartItemList() {
    return itemList;
  }

  public int getNumberOfItems() {
    return itemList.size();
  }

  public Iterator<CartItem> getAllCartItems() {
    return itemList.iterator();
  }

  public boolean containsItemId(String itemId) {
    return itemMap.containsKey(itemId);
  }

  public void cause(DomainEvent event) {
    mutate(event);
    eventCache.add(event);
  }

  // 基於特定事件改變聚合根“內部”狀態
  public void mutate(DomainEvent event) {
    if (event instanceof AddedItemToCartEvent) {
      AddedItemToCartEvent addedEvent = (AddedItemToCartEvent) event;
      Item item = addedEvent.getItem();
      boolean isInStock = addedEvent.getIsInStock();

      CartItem cartItem = itemMap.get(item.getItemId());
      if (cartItem == null) {
        cartItem = new CartItem();
        cartItem.setItem(item);
        cartItem.setQuantity(0);
        cartItem.setInStock(isInStock);
        itemMap.put(item.getItemId(), cartItem);
        itemList.add(cartItem);
      }
      cartItem.incrementQuantity();

    } else if (event instanceof RemoveItemFromCartEvent) {
      RemoveItemFromCartEvent RemoveItemEvent = (RemoveItemFromCartEvent) event;
      String itemId = RemoveItemEvent.getItemId();

      CartItem cartItem = itemMap.remove(itemId);
      if (cartItem == null) {
        Item item = null;
      } else {
        itemList.remove(cartItem);
        Item item = cartItem.getItem();
      }

    } else if (event instanceof IncrementItemToCartEvent) {
      IncrementItemToCartEvent incrementItemToCartEvent = (IncrementItemToCartEvent) event;
      String itemId = incrementItemToCartEvent.getItemId();

      CartItem cartItem = itemMap.get(itemId);
      cartItem.incrementQuantity();

    } else if (event instanceof InventoryUpdatedEvent) {
      // pass
    } else {
      throw new IllegalArgumentException();
    }
  }

  /**
   * Adds the item.
   *
   * @param item
   *          the item
   * @param isInStock
   *          the is in stock
   */
  public void addItem(Item item, boolean isInStock) {
    cause(new AddedItemToCartEvent(this.getStreamId(), Cart.class.getName(), item, isInStock, new Date().getTime()));

    // CartItem cartItem = itemMap.get(item.getItemId());
    // if (cartItem == null) {
    // cartItem = new CartItem();
    // cartItem.setItem(item);
    // cartItem.setQuantity(0);
    // cartItem.setInStock(isInStock);
    // itemMap.put(item.getItemId(), cartItem);
    // itemList.add(cartItem);
    // }
    // cartItem.incrementQuantity();
  }

  /**
   * Removes the item by id.
   *
   * @param itemId
   *          the item id
   *
   * @return the item
   */
  public void removeItemById(String itemId) {
    cause(new RemoveItemFromCartEvent(this.getStreamId(), Cart.class.getName(), itemId, new Date().getTime()));

    // CartItem cartItem = itemMap.remove(itemId);
    // if (cartItem == null) {
    // return null;
    // } else {
    // itemList.remove(cartItem);
    // return cartItem.getItem();
    // }
  }

  /**
   * Increment quantity by item id.
   *
   * @param itemId
   *          the item id
   */
  public void incrementQuantityByItemId(String itemId) {
    cause(new IncrementItemToCartEvent(this.getStreamId(), Cart.class.getName(), itemId, new Date().getTime()));
    // CartItem cartItem = itemMap.get(itemId);
    // cartItem.incrementQuantity();
  }

  public void setQuantityByItemId(String itemId, int quantity) {
    CartItem cartItem = itemMap.get(itemId);
    cartItem.setQuantity(quantity);
  }

  /**
   * Gets the sub total.
   *
   * @return the sub total
   */
  public BigDecimal getSubTotal() {
    return itemList.stream()
        .map(cartItem -> cartItem.getItem().getListPrice().multiply(new BigDecimal(cartItem.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public String getStreamId() {
    return Cart.class.getName() + "." + cartId;
  }

  public List<DomainEvent> getEvents() {
    return this.eventCache;
  }

  public void reset() {
    eventCache.clear();
  }
}
