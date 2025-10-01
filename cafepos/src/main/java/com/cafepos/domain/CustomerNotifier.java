package com.cafepos.domain;

public final class CustomerNotifier implements OrderObserver {
    @Override
    public void updated(Order order, String eventType) {
        if ("itemAdded".equals(eventType) || "ready".equals(eventType)) {
            System.out.println("[Customer] Dear customer, your Order #" + order.id() + " has been updated:\n" + eventType + ".");
        }
    }
}
