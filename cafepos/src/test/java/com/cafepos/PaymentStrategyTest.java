package com.cafepos;

import com.cafepos.catalog.Catalog;
import com.cafepos.catalog.InMemoryCatalog;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderIds;
import com.cafepos.domain.SimpleProduct;
import com.cafepos.payment.CardPayment;
import com.cafepos.payment.CashPayment;
import com.cafepos.payment.PaymentStrategy;
import com.cafepos.payment.WalletPayment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PaymentStrategyTest {
    private Catalog catalog;
    private Order order;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setup() {
        System.setOut(new PrintStream(outContent));

        catalog = new InMemoryCatalog();
        catalog.add(new SimpleProduct("P-ESP", "Espresso", Money.of(2.50)));
        catalog.add(new SimpleProduct("P-CCK", "Chocolate Cookie", Money.of(3.50)));

        order = new Order(OrderIds.next());
        order.addItem(new LineItem(catalog.findById("P-ESP").orElseThrow(), 2));
        order.addItem(new LineItem(catalog.findById("P-CCK").orElseThrow(), 1));
    }


    @Test
    void payment_strategy_called() {
        // Create a simple product
        SimpleProduct product = new SimpleProduct("A", "Test Product", Money.of(5.00));

        // Create an order and add an item
        Order order = new Order(1);
        order.addItem(new LineItem(product, 1));

        // boolean flag to verify that the strategy was called
        final boolean[] called = {false};

        // Create a fake PaymentStrategy
        PaymentStrategy fakeStrategy = o -> called[0] = true;

        // Pay using the fake strategy
        order.pay(fakeStrategy);

        // Assert that the strategy was called
        assertTrue(called[0], "Payment strategy should be called");
    }

    @Test
    void cashPayment_printsCorrectMessage() {
        order.pay(new CashPayment());

        String output = outContent.toString().trim();
        assertTrue(output.contains("[Cash] Customer paid 9.35 EUR"));
    }

    @Test
    void cardPayment_printsMaskedCardNumber() {
        order.pay(new CardPayment("1234567812341234"));

        String output = outContent.toString().trim();
        assertTrue(output.contains("[Card] Customer paid 9.35 EUR with card ****1234"));
    }

    @Test
    void walletPayment_printsCorrectWalletMessage() {
        order.pay(new WalletPayment("alice-wallet-01"));

        String output = outContent.toString().trim();
        assertTrue(output.contains("[Wallet] Customer paid 9.35 EUR using wallet alice-wallet-01"));
    }

    @Test
    void pay_throwsException_whenStrategyIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> order.pay(null));
        assertEquals("payment strategy required", ex.getMessage());
    }
}
