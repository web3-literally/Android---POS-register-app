package com.refresh.pos.domain.inventory;

/**
 * Created by Alpesh Makwana on 23/1/18.
 */

public class ReceiptProduct {

    private String name;
    private int qty;
    private double rate;
    private double amt;

    public ReceiptProduct(String name, int qty, double rate, double amt) {
        this.name = name;
        this.qty = qty;
        this.rate = rate;
        this.amt = amt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getAmt() {
        return amt;
    }

    public void setAmt(double amt) {
        this.amt = amt;
    }
}
