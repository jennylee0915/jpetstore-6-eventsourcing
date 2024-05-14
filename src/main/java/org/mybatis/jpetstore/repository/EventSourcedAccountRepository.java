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
package org.mybatis.jpetstore.repository;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.*;

import org.mybatis.jpetstore.core.EventStore;
import org.mybatis.jpetstore.core.event.AccountCreatedEvent;
import org.mybatis.jpetstore.core.event.DomainEvent;
import org.mybatis.jpetstore.domain.Account;

public class EventSourcedAccountRepository {
  private EventStore eventStore;
  private Map<String, String> accountIdCache;

  public EventSourcedAccountRepository(EventStore eventStore) {
    this.eventStore = eventStore;
    this.accountIdCache = new HashMap<>();
  }

  public String save(Account account) {
    String streamId = null;
    for (DomainEvent e : account.getEvents()) {
      try {
        streamId = e.getStreamId();
        eventStore.appendToStream(streamId, e);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    if (streamId != null) {
      accountIdCache.put(account.getUsername(), account.getAccountId());
    }
    return streamId;
  }

  private Account findByUsernameAndPassword(String username, String password) {
    String accountId = accountIdCache.get(username);
    if (accountId == null) {
      return null;
    }
    String streamId = Account.class.getName() + "." + accountId;
    List<DomainEvent> events = eventStore.getStream(streamId);
    Account account = new Account(accountId);
    for (DomainEvent event : events) {
      account.mutate(event);
    }
    if (account.getPassword().equals(password)) {
      return account;
    }
    return null;
  }

  public List<Account> findAll() {
    List<Account> accountList = new ArrayList<>();
    List<DomainEvent> events = eventStore.getAllStream();

    Map<String, List<DomainEvent>> eventsGroupByStreamId = events.stream()
        .filter(event -> event.getEntityType().equals(Account.class.getName()))
        .collect(groupingBy(DomainEvent::getStreamId, toList()));
    eventsGroupByStreamId.forEach((streamId, stream) -> {
      String[] streamSplitByDot = streamId.split("\\.");
      String accountId = streamSplitByDot[streamSplitByDot.length - 1];
      Account account = new Account(accountId);
      stream.forEach(account::mutate);
      accountList.add(account);
    });
    return accountList;
  }

  public Account findBy(String accountId) {
    String streamId = Account.class.getName() + "." + accountId;
    List<DomainEvent> events = eventStore.getStream(streamId);
    Account account = new Account(accountId);
    for (DomainEvent event : events) {
      account.mutate(event);
    }
    return account;
  }

  public Account findBy(String accountId, long version) {
    String streamId = Account.class.getName() + "." + accountId;
    List<DomainEvent> events = eventStore.getStream(streamId, version);
    Account account = new Account(accountId);
    for (DomainEvent event : events) {
      account.mutate(event);
    }
    return account;
  }

  public List<AccountCreatedEvent> getAccountEvents() {
    String streamPrefix = "org.mybatis.jpetstore.domain.Account.";

    List<AccountCreatedEvent> allEvents = new ArrayList<>();
    for (DomainEvent event : eventStore.getEventsByStreamPrefix(streamPrefix)) {
      if (event instanceof AccountCreatedEvent) {
        allEvents.add((AccountCreatedEvent) event);
      }
    }
    allEvents.sort(Comparator.comparing(e -> e.getTimestamp()));
    return allEvents;
  }

}
