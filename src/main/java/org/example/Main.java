package org.example;

import org.example.service.impl.PaymentServiceImpl;

import java.text.ParseException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        PaymentServiceImpl service = new PaymentServiceImpl(scanner);
        service.start();
    }
}