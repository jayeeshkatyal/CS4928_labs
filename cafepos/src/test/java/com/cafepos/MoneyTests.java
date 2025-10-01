package com.cafepos;

import com.cafepos.common.Money;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneyTests {

    @Test
    void money_addition() {
        assertEquals(Money.of(5.00),
                Money.of(2.00).add(Money.of(3.00)));
    }

    @Test
    void money_multiplication() {
        assertEquals(Money.of(5.00),
                Money.of(2.50).multiply(2));
    }
}
