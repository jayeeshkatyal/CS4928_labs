package com.cafepos.domain;

public final class KitchenDisplay implements OrderObserver {
    @Override
    public void updated(Order order, String eventType) {
        if ("itemAdded".equals(eventType)) {
            // Print quantity and product name for the last added item
            if (!order.items().isEmpty()) {
                var last = order.items().get(order.items().size() - 1);
                System.out.println("[Kitchen] Order #" + order.id() + ": " + last.quantity() + "x " + last.product().name() + " added");
            }
        } else if ("paid".equals(eventType)) {
            System.out.println("[Kitchen] Order #" + order.id() + ": Payment received");
        }
    }
}
