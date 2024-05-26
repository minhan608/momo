package org.example.model;

import org.example.constant.BillState;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Bill {

    private int id;
    private String type;
    private double amount;
    private Date dueDate;
    private BillState state;
    private String provider;
    private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    public Bill(int id, String type, double amount, String dueDate, BillState state, String provider) throws  ParseException {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.dueDate = formatter.parse(dueDate);
        this.state = state;
        this.provider = provider;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public BillState getState() {
        return state;
    }

    public void setState(BillState state) {
        this.state = state;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    @Override
    public String toString() {
        return id + ". " + type + " " + amount + " " + formatter.format(dueDate) + " " + state + " " + provider;
    }

}
