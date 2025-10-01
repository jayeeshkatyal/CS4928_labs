package com.cafepos.domain;

import com.cafepos.common.Money;
import com.cafepos.payment.PaymentStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public final class Order implements OrderPublisher {
    private final long id;
    private final List<LineItem> items = new ArrayList<>();
    // 1) Maintain subscriptions
    private final List<OrderObserver> observers = new ArrayList<>();

    public Order(long id) {
        this.id = id;
    }

    public long id() {
        return id;
    }

    public List<LineItem> items() {
        return items;
    }

    public void addItem(LineItem li) {
        if (li == null) throw new IllegalArgumentException("line item required");
        if (li.quantity() <= 0) throw new IllegalArgumentException("quantity must be > 0");
        items.add(li);
    notifyObservers("itemAdded");
    }

    public Money subtotal() {
        return items.stream()
                .map(LineItem::lineTotal)
                .reduce(Money.zero(), Money::add);
    }

    public Money taxAtPercent(int percent) {
        if (percent < 0) throw new IllegalArgumentException("percent cannot be negative");

        BigDecimal rate = BigDecimal.valueOf(percent)
                .divide(BigDecimal.valueOf(100));
        BigDecimal tax = subtotal().amount().multiply(rate);

        return Money.of(tax);
    }

    public Money totalWithTax(int percent) {
        return subtotal().add(taxAtPercent(percent));
    }

    public void pay(PaymentStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("payment strategy required");
        }
        strategy.pay(this);
    notifyObservers("paid");
    }

    // 2) Publish events
    private void notifyObservers(String eventType) {
        for (OrderObserver o : observers) {
            o.updated(this, eventType);
        }
    }

    // Implementation of OrderPublisher interface method
    @Override
    public void notifyObservers(Order order, String eventType) {
        for (OrderObserver o : observers) {
            o.updated(order, eventType);
        }
    }

    // 3) Observer management
    @Override
    public void register(OrderObserver o) {
        if (o == null) return;
        if (!observers.contains(o)) {
            observers.add(o);
        }
    }

    @Override
    public void unregister(OrderObserver o) {
        observers.remove(o);
    }

    public void markReady() {
        notifyObservers("ready");
    }
}
