package com.cafepos.domain;

public class OrderIds {
    private static long nextId = 1000;
    public static synchronized long next() {
        return ++nextId;
    }
}
