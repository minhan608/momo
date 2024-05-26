package org.example.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.example.constant.BillState;
import org.example.constant.PaymentState;
import org.example.model.Bill;
import org.example.model.Payment;
import org.example.service.PaymentService;

public class PaymentServiceImpl implements PaymentService {
    private Scanner scanner;
    private List<Bill> bills = new ArrayList<>();
    private List<Payment> payments = new ArrayList<>();
    private double balance = 0;
    private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    public PaymentServiceImpl(Scanner scanner) {
        this.scanner = scanner;
        init();
    }

    private void init() {
        try {
            bills.add(new Bill(1, "ELECTRIC", 200000, "25/10/2020", BillState.NOT_PAID, "EVN HCMC"));
            bills.add(new Bill(2, "WATER", 175000, "30/10/2020", BillState.NOT_PAID, "SAVACO HCMC"));
            bills.add(new Bill(3, "INTERNET", 800000, "30/11/2020", BillState.NOT_PAID, "VNPT"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() throws ParseException {

        while (true) {
            String cmd = scanner.nextLine();
            String[] request = cmd.split(" ");
            switch (request[0]) {
                case "CASH_IN":
                    cashIn(Double.parseDouble(request[1]));
                    break;
                case "LIST_BILL":
                    listBills();
                    break;
                case "CREATE_BILL":
                    createBill(new Bill(Integer.parseInt(request[1]), request[2], Double.parseDouble(request[3]), request[4], BillState.NOT_PAID, request[5]));
                    break;
                case "UPDATE_BILL":
                    updateBill(new Bill(Integer.parseInt(request[1]), request[2], Double.parseDouble(request[3]), request[4], BillState.valueOf(request[5]), request[6]));
                    break;
                case "DELETE_BILL":
                    deleteBill(Integer.parseInt(request[1]));
                    break;
                case "VIEW_BILL":
                    viewBill(Integer.parseInt(request[1]));
                    break;
                case "PAY":
                    payBills(Arrays.copyOfRange(request, 1, request.length));
                    break;
                case "DUE_DATE":
                    listDueDates();
                    break;
                case "SCHEDULE":
                    schedulePayment(Integer.parseInt(request[1]), request[2]);
                    break;
                case "LIST_PAYMENT":
                    listPayments();
                    break;
                case "SEARCH_BILL_BY_PROVIDER":
                    searchBillByProvider(request[1]);
                    break;
                case "EXIT":
                    System.out.println("Good bye!");
                    return;
                default:
                    System.out.println("Invalid command!");
            }
        }
    }

    public void cashIn(double amount) {
        balance += amount;
        System.out.println("Your available balance: " + balance);
    }

    public void listBills() {
        System.out.println("Bill No. Type Amount Due Date State PROVIDER");
        for (Bill bill : bills) {
            System.out.println(bill);
        }
    }

    public void createBill(Bill bill) {
        bills.add(bill);
        System.out.println("Bill created successfully: " + bill);
    }

    public void updateBill(Bill updatedBill) {
        Bill bill = findBillById(updatedBill.getId());
        if (bill != null) {
            try {
                bill.setType(updatedBill.getType());
                bill.setAmount(updatedBill.getAmount());
                bill.setDueDate(formatter.parse(formatter.format(updatedBill.getDueDate())));
                bill.setState(updatedBill.getState());
                bill.setProvider(updatedBill.getProvider());
                System.out.println("Bill updated successfully: " + bill);
            } catch (ParseException e) {
                System.out.println("Invalid updated data!");
            }
        } else {
            System.out.println("Bill not found!");
        }
    }

    public void deleteBill(int id) {
        Bill bill = findBillById(id);
        if (bill != null) {
            bills.remove(bill);
            System.out.println("Bill deleted successfully: " + bill);
        } else {
            System.out.println("Bill not found!");
        }
    }

    public void viewBill(int id) {
        Bill bill = findBillById(id);
        if (bill != null) {
            System.out.println(bill);
        } else {
            System.out.println("Bill not found!");
        }
    }

    public void payBills(String[] billIds) {
        List<Bill> billsToPay = new ArrayList<>();
        for (String billIdStr : billIds) {
            int billId = Integer.parseInt(billIdStr);
            Bill bill = findBillById(billId);
            if (bill != null) {
                billsToPay.add(bill);
            } else {
                System.out.println("Sorry! Not found a bill with such id: " + billId);
                return;
            }
        }

        billsToPay.sort(Comparator.comparing(Bill::getDueDate));

        double totalAmount = billsToPay.stream().mapToDouble(Bill::getAmount).sum();
        if (balance < totalAmount) {
            System.out.println("Sorry! Not enough fund to proceed with payment.");
            return;
        }

        for (Bill bill : billsToPay) {
            if (balance >= bill.getAmount()) {
                balance -= bill.getAmount();
                bill.setState(BillState.PAID);
                payments.add(new Payment(bill.getAmount(), new Date(), PaymentState.PROCESSED, bill.getId()));
                System.out.println("Payment has been completed for Bill with id " + bill.getId() + ".");
                System.out.println("Your current balance is: " + balance);
            }
        }
    }

    public void listDueDates() {
        System.out.println("Bill No. Type Amount Due Date State PROVIDER");
        for (Bill bill : bills) {
            if (bill.getState() == BillState.NOT_PAID) {
                System.out.println(bill);
            }
        }
    }

    public void schedulePayment(int billId, String date) {
        Bill bill = findBillById(billId);
        if (bill != null) {
            if (bill.getState() == BillState.PAID) {
                System.out.println("Bill with id " + billId + " has already been paid. Cannot schedule payment.");
                return;
            }
            try {
                Date scheduledDate = formatter.parse(date);
                payments.add(new Payment(bill.getAmount(), scheduledDate, PaymentState.PENDING, billId));
                System.out.println("Payment for bill id " + billId + " is scheduled on " + date);
            } catch (ParseException e) {
                System.out.println("Invalid date format!");
            }
        } else {
            System.out.println("Bill not found!");
        }
    }

    public void listPayments() {
        System.out.println("No. Amount Payment Date State Bill Id");
        int i = 1;
        for (Payment payment : payments) {
            System.out.println(i + ". " + payment);
            i++;
        }
    }

    public void searchBillByProvider(String provider) {
        System.out.println("Bill No. Type Amount Due Date State PROVIDER");
        for (Bill bill : bills) {
            if (bill.getProvider().equals(provider)) {
                System.out.println(bill);
            }
        }
    }

    private Bill findBillById(int billId) {
        for (Bill bill : bills) {
            if (bill.getId() == billId) {
                return bill;
            }
        }
        return null;
    }
}
