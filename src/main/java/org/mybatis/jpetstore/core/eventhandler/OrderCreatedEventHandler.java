package org.mybatis.jpetstore.core.eventhandler;

import org.mybatis.jpetstore.core.event.DomainEvent;
import org.mybatis.jpetstore.core.event.OrderCreatedEvent;
import org.mybatis.jpetstore.domain.Order;
import org.mybatis.jpetstore.mapper.LineItemMapper;
import org.mybatis.jpetstore.mapper.OrderMapper;

public class OrderCreatedEventHandler implements DomainEventHandler<OrderCreatedEvent>{
    private OrderMapper orderMapper;
    private LineItemMapper lineItemMapper;

    @Override
    public void handle(OrderCreatedEvent event) {
        /** x錯誤的方法x
        Order order = event.getOrder();

        
        orderMapper.insertOrder(order);
        orderMapper.insertOrderStatus(order);

    
        order.getLineItems().forEach(lineItem -> {
            lineItem.setOrderId(order.getOrderId());
            lineItemMapper.insertLineItem(lineItem);
        });
         **/
    }

    public OrderCreatedEventHandler(OrderMapper orderMapper){
        this.orderMapper = orderMapper;
    }

}
