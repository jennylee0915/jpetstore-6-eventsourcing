package org.mybatis.jpetstore.repository;

import org.mybatis.jpetstore.core.EventStore;
import org.mybatis.jpetstore.core.event.DomainEvent;
import org.mybatis.jpetstore.domain.Category;

import java.util.List;

public class EventSourcedCategoryRepository {
    public EventSourcedCategoryRepository(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    private EventStore eventStore;

    public String save(Category category) {
        String streamId = null;
        for (DomainEvent e : category.getEvents()) {
            try {
                streamId = e.getStreamId();
                eventStore.appendToStream(streamId,e);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return streamId;
    }

    public Category findBy(String categoryId) {
        String streamId = Category.class.getName() + "." + categoryId;
        List<DomainEvent> events = eventStore.getStream(streamId);
        Category category = new Category(categoryId);

        for (DomainEvent event : events) {
            // System.out.println("Mutate because of the event: " + event);
            category.mutate(event);
            //System.out.println("Object states after the mutate: " + category);
        }
        return category;
    }

}
