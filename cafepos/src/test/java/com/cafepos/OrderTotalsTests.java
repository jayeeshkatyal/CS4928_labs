package com.cafepos;

import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.domain.SimpleProduct;
import com.cafepos.domain.OrderObserver;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderTotalsTests {

    @Test
    void order_totals() {
        var p1 = new SimpleProduct("A", "A", Money.of(2.50));
        var p2 = new SimpleProduct("B", "B", Money.of(3.50));

        var o = new Order(1);
        o.addItem(new LineItem(p1, 2));
        o.addItem(new LineItem(p2, 1));

        assertEquals(Money.of(8.50), o.subtotal());
        assertEquals(Money.of(0.85), o.taxAtPercent(10));
        assertEquals(Money.of(9.35), o.totalWithTax(10));
    }

    @Test
    void observers_notified_on_item_add() {
        var p = new SimpleProduct("A", "A", Money.of(2));
        var o = new Order(1);
        o.addItem(new LineItem(p, 1)); // baseline
        List<String> events = new ArrayList<>();
        o.register((order, evt) -> events.add(evt));
        o.addItem(new LineItem(p, 1));
        assertTrue(events.contains("itemAdded"));
    }

    @Test
    void multiple_observers_receive_ready_event() {
        var p = new SimpleProduct("A", "A", Money.of(2));
        var o = new Order(1);
        List<String> events1 = new ArrayList<>();
        List<String> events2 = new ArrayList<>();
        OrderObserver obs1 = (order, evt) -> events1.add(evt);
        OrderObserver obs2 = (order, evt) -> events2.add(evt);
        o.register(obs1);
        o.register(obs2);
        o.markReady();
        assertTrue(events1.contains("ready"));
        assertTrue(events2.contains("ready"));
    }
}
