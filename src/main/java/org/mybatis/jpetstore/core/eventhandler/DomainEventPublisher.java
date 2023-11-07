package org.mybatis.jpetstore.core.eventhandler;

import org.mybatis.jpetstore.core.event.DomainEvent;

import java.util.HashMap;
import java.util.Map;

public class DomainEventPublisher {
    private Map<Class<?>, DomainEventHandler<?>> handlers = new HashMap<>();

    public <T extends DomainEvent> void registerHandler(Class<T> eventType, DomainEventHandler<T> handler){
        handlers.put(eventType, handler);
    }

    public <T extends DomainEvent> void publish(T event){
        DomainEventHandler<T> handler = (DomainEventHandler<T>) handlers.get(event.getClass());
        if (handler != null){
            handler.handle(event);
        }
    }
}
