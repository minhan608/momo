package org.example.model;

import org.example.constant.PaymentState;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Payment {
    private double amount;
    private Date paymentDate;
    private PaymentState state;
    private int billId;
    private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    public Payment(double amount, Date paymentDate, PaymentState state, int billId) {
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.state = state;
        this.billId = billId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public PaymentState getState() {
        return state;
    }

    public void setState(PaymentState state) {
        this.state = state;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    @Override
    public String toString() {
        return amount + " " + formatter.format(paymentDate) + " " + state + " " + billId;
    }
}
