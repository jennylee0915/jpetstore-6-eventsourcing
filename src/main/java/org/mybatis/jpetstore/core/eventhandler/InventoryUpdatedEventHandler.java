package org.mybatis.jpetstore.core.eventhandler;

import org.mybatis.jpetstore.core.event.InventoryChangeEvent;
import org.mybatis.jpetstore.mapper.ItemMapper;

import java.util.HashMap;
import java.util.Map;

public class InventoryUpdatedEventHandler implements DomainEventHandler<InventoryChangeEvent> {
    private ItemMapper itemMapper;

    @Override
    public void handle(InventoryChangeEvent event) {
        /** x錯誤的方法x
        Map<String, Object> param = new HashMap<>(2);
        param.put("itemId", event.getItemId());
        param.put("increment", event.getChangeInQuantity());
        itemMapper.updateInventoryQuantity(param);
         **/
    }
    
    public InventoryUpdatedEventHandler(ItemMapper itemMapper){
        this.itemMapper = itemMapper;
    }
}
