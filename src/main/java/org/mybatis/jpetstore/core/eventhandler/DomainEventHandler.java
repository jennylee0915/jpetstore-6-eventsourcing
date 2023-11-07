package org.mybatis.jpetstore.core.eventhandler;

import org.mybatis.jpetstore.core.event.DomainEvent;

public interface DomainEventHandler<T extends DomainEvent> {
    void handle(T event);

}
