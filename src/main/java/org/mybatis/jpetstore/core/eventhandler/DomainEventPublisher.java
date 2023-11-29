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
package org.mybatis.jpetstore.core.eventhandler;

import java.util.HashMap;
import java.util.Map;

import org.mybatis.jpetstore.core.event.DomainEvent;

public class DomainEventPublisher {
  private Map<Class<?>, DomainEventHandler<?>> handlers = new HashMap<>();

  public <T extends DomainEvent> void registerHandler(Class<T> eventType, DomainEventHandler<T> handler) {
    handlers.put(eventType, handler);
  }

  public <T extends DomainEvent> void publish(T event) {
    DomainEventHandler<T> handler = (DomainEventHandler<T>) handlers.get(event.getClass());
    if (handler != null) {
      handler.handle(event);
    }
  }
}
