package com.cafepos.payment;
import com.cafepos.domain.Order;
public class WalletPayment implements PaymentStrategy {
    private final String walletId;

    public WalletPayment(String walletId) {
        if (walletId == null || walletId.isBlank()) {
            throw new IllegalArgumentException("wallet id required");
        }
        this.walletId = walletId;
    }

@Override
public void pay(Order order) {
    System.out.println("[Wallet] Customer paid " + order.totalWithTax(10) + " EUR using wallet " + walletId);
    }
}
